package com.glyph.play

import com.google.android.gms.common.api.{ResultCallback, Scope, Api, GoogleApiClient}
import com.google.android.gms.common.api.GoogleApiClient.{OnConnectionFailedListener, ConnectionCallbacks, Builder}
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import android.app.Activity
import com.glyph._scala.lib.util.Logging
import com.google.android.gms.games.{GamesActivityResultCodes, Games}
import scalaz._
import Scalaz._
import com.google.android.gms.drive.query.Query
import com.google.android.gms.games.leaderboard.LeaderboardVariant
import com.google.android.gms.games.leaderboard.Leaderboards.LoadScoresResult
import com.glyph.make3.R

/**
 * @author glyph
 */
trait PlayManagerActivity extends Activity with Logging {

  import PlayManagerHelper._

  val STATE_RESOLVING_ERROR = "resolving_error"
  var client: GoogleApiClient = null
  var resolvingError = false
  val REQUEST_RESOLVE_ERROR = 1001
  implicit val context = this

  def api: Seq[Api]

  def scopes: Seq[Scope]

  override def onSaveInstanceState(outState: Bundle): Unit = {
    super.onSaveInstanceState(outState)
    outState.putBoolean(STATE_RESOLVING_ERROR, resolvingError)
  }

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    if (savedInstanceState != null) {
      resolvingError = savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false)
    }
    if (client == null) {
      // i hate this kind of state full operations...
      client = (new Builder(context)
        <| (b => api.foreach(b.addApi))
        <| (b => scopes.foreach(b.addScope)))
        .setViewForPopups(findViewById(android.R.id.content))
        .addConnectionCallbacks(new ConnectionCallbacks {
        override def onConnected(bundle: Bundle) {
          log("connected to google api")
          PlayManagerActivity.this.onConnected(bundle)
        }

        override def onConnectionSuspended(cause: Int) {
          log(s"connection to google api is suspended:cause => ${connectionCallbackCauses(cause)}")
          onSuspended(cause)
        }
      })
        .addOnConnectionFailedListenerF(result => {
        err("connection failed")
        err(result.toString)
        err("connection result is :" + connectionResultCauses(result.getErrorCode))
        if (result.hasResolution) {
          resolvingError = true
          result.startResolutionForResult(context, REQUEST_RESOLVE_ERROR)
          //this may start another activity to login
        }
      })
        .build()
    }
  }

  override def onStart(): Unit = {
    super.onStart()
    client.connect()
  }

  override def onStop(): Unit = {
    super.onStop()
    client.disconnect()
  }

  //an exception in this method causes a RuntimeException "Failure delivering result ResultInfo{....}"
  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    log("ActivityResukt" +(requestCode, resultCode, data))
    if (requestCode == REQUEST_RESOLVE_ERROR) {
      resolvingError = false
      log("ActivityResult:RequestResolveError")
      if (resultCode == Activity.RESULT_OK) {
        log("ActivityResult:RESULT_OK")
        if (!client.isConnecting && client.isConnected) {
          log("ActivityResult:attempt to connect again")
          client.connect() //connect again.
        }
      } else {
        err("ActivityResult:failed=>" + gamesActivityResultCodes.lift(resultCode))
        // maybe i should show some error dialog
      }
    }
  }

  def onConnected(bundle: Bundle) {}

  def onSuspended(cause: Int) {}
}

trait GameService extends PlayManagerActivity {
  override def api: Seq[Api] = Seq(Games.API)

  override def scopes: Seq[Scope] = Seq(Games.SCOPE_GAMES)

  lazy val highScoreId = getResources.getString(R.string.leaderboard_HighScore)

  val REQUEST_SHOW_ALL_LEADERBOARD = 1006

  def queryLeaderBoard() {
    log("query get leader board")
    import LeaderboardVariant._
    startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(client),REQUEST_SHOW_ALL_LEADERBOARD)
    /*
    // I want this api to be in a future form..
    val pendingResult = Games.Leaderboards.loadTopScores(client, highScoreId, TIME_SPAN_ALL_TIME, COLLECTION_PUBLIC, 10)
    log("created pending result")
    if (pendingResult != null) {
      pendingResult.setResultCallback(new ResultCallback[LoadScoresResult] {
        override def onResult(result: LoadScoresResult) {
          log("result=>")
          log(result.getLeaderboard)
          import scala.collection.JavaConversions._
          result.getScores <| (_.foreach(log)) <| (_.close())
          log("result<=")
        }
      })
    }
    */
  }
}

object PlayManagerHelper {
  type -->[P, R] = PartialFunction[P, R]
  lazy val connectionCallbackCauses = {
    import ConnectionCallbacks._
    //TODO write a macro to do this.
    Map(CAUSE_NETWORK_LOST -> "CAUSE_NETWORK_LOST", CAUSE_SERVICE_DISCONNECTED -> "CAUSE_SERVICE_DISCONNECTED")
  }
  lazy val connectionResultCauses = {
    import ConnectionResult._
    Map(
      DEVELOPER_ERROR -> "DEVELOPER_ERROR",
      ConnectionResult.DATE_INVALID -> "DATE_INVALID",
      ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED -> "DRIVE_EXTERNAL_STORAGE_REQUIRED",
      ConnectionResult.INTERNAL_ERROR -> "INTERNAL_ERROR",
      ConnectionResult.INVALID_ACCOUNT -> "INVALID_ACCOUNT",
      ConnectionResult.LICENSE_CHECK_FAILED -> "LICENSE_CHECK_FAILED",
      ConnectionResult.NETWORK_ERROR -> "NETWORK_ERROR",
      ConnectionResult.RESOLUTION_REQUIRED -> "RESOLUTION_REQUIRED",
      ConnectionResult.SERVICE_DISABLED -> "SERVICE_DISABLED",
      ConnectionResult.SERVICE_INVALID -> "SERVICE_INVALID",
      ConnectionResult.SERVICE_MISSING -> "SERVICE_MISSING",
      ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED -> "SERVICE_VERSION_UPDATE_REQUIRED",
      ConnectionResult.SIGN_IN_REQUIRED -> "SIGN_IN_REQUIRED",
      ConnectionResult.SUCCESS -> "SUCCESS"
    )
  }
  lazy val gamesActivityResultCodes = Map(
    GamesActivityResultCodes.RESULT_APP_MISCONFIGURED -> "APP_MISCONFIGURED",
    GamesActivityResultCodes.RESULT_LEFT_ROOM -> "LEFT_ROOM",
    GamesActivityResultCodes.RESULT_LICENSE_FAILED -> "LICENSE_FAILED",
    GamesActivityResultCodes.RESULT_NETWORK_FAILURE -> "NETWORK_FAILURE",
    GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED -> "RECONNECT_REQUIRED",
    GamesActivityResultCodes.RESULT_SIGN_IN_FAILED -> "SIGN_IN_FAILED"
  )

  implicit class ApiBuilder(builder: Builder) {
    def addOnConnectionFailedListenerF(cb: ConnectionResult => Unit): Builder = {
      builder.addOnConnectionFailedListener(new OnConnectionFailedListener {
        override def onConnectionFailed(result: ConnectionResult) = cb(result)
      })
    }
  }

  /**
   * Warning: If you try to make Play Games services SDK calls for
   * an unpublished game by using an account that's not listed
   * as a test account, the game services will behave
   * as if the game did not exist and you'll get back the
   * ConnectionResult.SIGN_IN_REQUIRED return code.
   * If you attempt to launch ConnectionResult.startResolutionForResult(),
   * you'll get back GamesActivityResultCodes.RESULT_SIGN_IN_FAILED.
   */
}
