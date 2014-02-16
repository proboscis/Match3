package scala.com.glyph.play

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.{OnConnectionFailedListener, ConnectionCallbacks, Builder}
import android.content.Context
import com.google.android.gms.drive.Drive
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import android.app.Activity
import com.glyph._scala.lib.util.Logging
import com.google.android.gms.common

/**
 * @author glyph
 */
object PlayManager extends Logging{
  import PlayManagerHelper._
  var client :GoogleApiClient = null
  def initialize(implicit context:Context){
    client = new Builder(context)
      .addApi(Drive.API)
      .addScope(Drive.SCOPE_FILE)
      .addConnectionCallbacks(new ConnectionCallbacks {
        override def onConnected(bundle:Bundle){
          log("connected to google api")
        }
        override def onConnectionSuspended(cause:Int){
          log(s"connection to google api is suspended:cause => ${connectionCallbackCauses(cause)}")
        }
    })
      .addOnConnectionFailedListenerF(result => {
      log("connection failed")
      log(result.toString)
      log("connection result is :" + connectionResultCauses(result.getErrorCode))
      if(result.hasResolution){
        result.getResolution.send()
        //this may start another activity to login
      }
    })
      .build()
  }
  def connect(){
    log("connect")
    client.connect()
  }
  def disconnect(){
    log("disconnect")
    client.disconnect()
  }
}
trait PlayManagerActivity extends Activity{
  implicit val context = this
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    PlayManager.initialize
  }

  override def onStart(): Unit = {
    super.onStart()
    PlayManager.connect()
  }

  override def onStop(): Unit = {
    super.onStop()
    PlayManager.disconnect()
  }
}

object PlayManagerHelper{
  val connectionCallbackCauses = {
    import ConnectionCallbacks._
    //TODO write a macro to do this.
    Map(CAUSE_NETWORK_LOST->"CAUSE_NETWORK_LOST",CAUSE_SERVICE_DISCONNECTED->"CAUSE_SERVICE_DISCONNECTED")
  }
  val connectionResultCauses = {
    import ConnectionResult._
    Map(
      DEVELOPER_ERROR->"DEVELOPER_ERROR",
      ConnectionResult.DATE_INVALID->"DATE_INVALID",
      ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED->"DRIVE_EXTERNAL_STORAGE_REQUIRED",
      ConnectionResult.INTERNAL_ERROR->"INTERNAL_ERROR",
      ConnectionResult.INVALID_ACCOUNT->"INVALID_ACCOUNT",
      ConnectionResult.LICENSE_CHECK_FAILED->"LICENSE_CHECK_FAILED",
      ConnectionResult.NETWORK_ERROR->"NETWORK_ERROR",
      ConnectionResult.RESOLUTION_REQUIRED->"RESOLUTION_REQUIRED",
      ConnectionResult.SERVICE_DISABLED->"SERVICE_DISABLED",
      ConnectionResult.SERVICE_INVALID->"SERVICE_INVALID",
      ConnectionResult.SERVICE_MISSING->"SERVICE_MISSING",
      ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED->"SERVICE_VERSION_UPDATE_REQUIRED",
      ConnectionResult.SIGN_IN_REQUIRED->"SIGN_IN_REQUIRED",
      ConnectionResult.SUCCESS->"SUCCESS"
    )
  }
  implicit class ApiBuilder(builder:Builder){
    def addOnConnectionFailedListenerF(cb:ConnectionResult => Unit):Builder = {
      builder.addOnConnectionFailedListener(new OnConnectionFailedListener {
        override def onConnectionFailed(result:ConnectionResult) = cb(result)
      })
    }
  }
  
}
