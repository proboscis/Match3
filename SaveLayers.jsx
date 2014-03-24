




<!DOCTYPE html>
<html class=" ">
  <head prefix="og: http://ogp.me/ns# fb: http://ogp.me/ns/fb# object: http://ogp.me/ns/object# article: http://ogp.me/ns/article# profile: http://ogp.me/ns/profile#">
    <meta charset='utf-8'>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    
    
    <title>Photoshop-Export-Layers-as-Images/SaveLayers.jsx at master · jwa107/Photoshop-Export-Layers-as-Images · GitHub</title>
    <link rel="search" type="application/opensearchdescription+xml" href="/opensearch.xml" title="GitHub" />
    <link rel="fluid-icon" href="https://github.com/fluidicon.png" title="GitHub" />
    <link rel="apple-touch-icon" sizes="57x57" href="/apple-touch-icon-114.png" />
    <link rel="apple-touch-icon" sizes="114x114" href="/apple-touch-icon-114.png" />
    <link rel="apple-touch-icon" sizes="72x72" href="/apple-touch-icon-144.png" />
    <link rel="apple-touch-icon" sizes="144x144" href="/apple-touch-icon-144.png" />
    <meta property="fb:app_id" content="1401488693436528"/>

      <meta content="@github" name="twitter:site" /><meta content="summary" name="twitter:card" /><meta content="jwa107/Photoshop-Export-Layers-as-Images" name="twitter:title" /><meta content="Photoshop-Export-Layers-as-Images - This script allows you to export your layers as individual JPGs / PNGs at a speed much faster than the built-in script from adobe." name="twitter:description" /><meta content="https://avatars3.githubusercontent.com/u/2524299?s=400" name="twitter:image:src" />
<meta content="GitHub" property="og:site_name" /><meta content="object" property="og:type" /><meta content="https://avatars3.githubusercontent.com/u/2524299?s=400" property="og:image" /><meta content="jwa107/Photoshop-Export-Layers-as-Images" property="og:title" /><meta content="https://github.com/jwa107/Photoshop-Export-Layers-as-Images" property="og:url" /><meta content="Photoshop-Export-Layers-as-Images - This script allows you to export your layers as individual JPGs / PNGs at a speed much faster than the built-in script from adobe." property="og:description" />

    <link rel="assets" href="https://github.global.ssl.fastly.net/">
    <link rel="conduit-xhr" href="https://ghconduit.com:25035/">
    <link rel="xhr-socket" href="/_sockets" />

    <meta name="msapplication-TileImage" content="/windows-tile.png" />
    <meta name="msapplication-TileColor" content="#ffffff" />
    <meta name="selected-link" value="repo_source" data-pjax-transient />
    <meta content="collector.githubapp.com" name="octolytics-host" /><meta content="collector-cdn.github.com" name="octolytics-script-host" /><meta content="github" name="octolytics-app-id" /><meta content="72A0B809:5A9E:2CA5638:53301F37" name="octolytics-dimension-request_id" />
    

    
    
    <link rel="icon" type="image/x-icon" href="https://github.global.ssl.fastly.net/favicon.ico" />

    <meta content="authenticity_token" name="csrf-param" />
<meta content="am/TyzHZkMQZRWNYFvjsmq3BvKATWm+MItu3wQaQcLY=" name="csrf-token" />

    <link href="https://github.global.ssl.fastly.net/assets/github-dd2e4ab77e94896e4418f4dd1d11ce1efb595fd1.css" media="all" rel="stylesheet" type="text/css" />
    <link href="https://github.global.ssl.fastly.net/assets/github2-91575e4b8f601c887ca6b13c6f78d06f03af775f.css" media="all" rel="stylesheet" type="text/css" />
    


        <script crossorigin="anonymous" src="https://github.global.ssl.fastly.net/assets/frameworks-1fdcfaa86258d75070622ace871ef84ed6e72cf0.js" type="text/javascript"></script>
        <script async="async" crossorigin="anonymous" src="https://github.global.ssl.fastly.net/assets/github-7e1a088b2bca0ded37a4fba91c2691508dc734bb.js" type="text/javascript"></script>
        
        
      <meta http-equiv="x-pjax-version" content="0778df758767b5af5f882e3e85af2587">

        <link data-pjax-transient rel='permalink' href='/jwa107/Photoshop-Export-Layers-as-Images/blob/c63fe3be4ec3c2c056f6ee1e80bacc28e5bce0f3/SaveLayers.jsx'>

  <meta name="description" content="Photoshop-Export-Layers-as-Images - This script allows you to export your layers as individual JPGs / PNGs at a speed much faster than the built-in script from adobe." />

  <meta content="2524299" name="octolytics-dimension-user_id" /><meta content="jwa107" name="octolytics-dimension-user_login" /><meta content="11925263" name="octolytics-dimension-repository_id" /><meta content="jwa107/Photoshop-Export-Layers-as-Images" name="octolytics-dimension-repository_nwo" /><meta content="true" name="octolytics-dimension-repository_public" /><meta content="false" name="octolytics-dimension-repository_is_fork" /><meta content="11925263" name="octolytics-dimension-repository_network_root_id" /><meta content="jwa107/Photoshop-Export-Layers-as-Images" name="octolytics-dimension-repository_network_root_nwo" />
  <link href="https://github.com/jwa107/Photoshop-Export-Layers-as-Images/commits/master.atom" rel="alternate" title="Recent Commits to Photoshop-Export-Layers-as-Images:master" type="application/atom+xml" />

  </head>


  <body class="logged_out  env-production windows vis-public page-blob">
    <a href="#start-of-content" class="accessibility-aid js-skip-to-content">Skip to content</a>
    <div class="wrapper">
      
      
      
      


      
      <div class="header header-logged-out">
  <div class="container clearfix">

    <a class="header-logo-wordmark" href="https://github.com/">
      <span class="mega-octicon octicon-logo-github"></span>
    </a>

    <div class="header-actions">
        <a class="button primary" href="/join">Sign up</a>
      <a class="button signin" href="/login?return_to=%2Fjwa107%2FPhotoshop-Export-Layers-as-Images%2Fblob%2Fmaster%2FSaveLayers.jsx">Sign in</a>
    </div>

    <div class="command-bar js-command-bar  in-repository">

      <ul class="top-nav">
          <li class="explore"><a href="/explore">Explore</a></li>
        <li class="features"><a href="/features">Features</a></li>
          <li class="enterprise"><a href="https://enterprise.github.com/">Enterprise</a></li>
          <li class="blog"><a href="/blog">Blog</a></li>
      </ul>
        <form accept-charset="UTF-8" action="/search" class="command-bar-form" id="top_search_form" method="get">

<input type="text" data-hotkey="/ s" name="q" id="js-command-bar-field" placeholder="Search or type a command" tabindex="1" autocapitalize="off"
    
    
      data-repo="jwa107/Photoshop-Export-Layers-as-Images"
      data-branch="master"
      data-sha="a20088379ab343e899cd0de10cfe6eb139d78a94"
  >

    <input type="hidden" name="nwo" value="jwa107/Photoshop-Export-Layers-as-Images" />

    <div class="select-menu js-menu-container js-select-menu search-context-select-menu">
      <span class="minibutton select-menu-button js-menu-target" role="button" aria-haspopup="true">
        <span class="js-select-button">This repository</span>
      </span>

      <div class="select-menu-modal-holder js-menu-content js-navigation-container" aria-hidden="true">
        <div class="select-menu-modal">

          <div class="select-menu-item js-navigation-item js-this-repository-navigation-item selected">
            <span class="select-menu-item-icon octicon octicon-check"></span>
            <input type="radio" class="js-search-this-repository" name="search_target" value="repository" checked="checked" />
            <div class="select-menu-item-text js-select-button-text">This repository</div>
          </div> <!-- /.select-menu-item -->

          <div class="select-menu-item js-navigation-item js-all-repositories-navigation-item">
            <span class="select-menu-item-icon octicon octicon-check"></span>
            <input type="radio" name="search_target" value="global" />
            <div class="select-menu-item-text js-select-button-text">All repositories</div>
          </div> <!-- /.select-menu-item -->

        </div>
      </div>
    </div>

  <span class="help tooltipped tooltipped-s" aria-label="Show command bar help">
    <span class="octicon octicon-question"></span>
  </span>


  <input type="hidden" name="ref" value="cmdform">

</form>
    </div>

  </div>
</div>



      <div id="start-of-content" class="accessibility-aid"></div>
          <div class="site" itemscope itemtype="http://schema.org/WebPage">
    
    <div class="pagehead repohead instapaper_ignore readability-menu">
      <div class="container">
        

<ul class="pagehead-actions">


  <li>
    <a href="/login?return_to=%2Fjwa107%2FPhotoshop-Export-Layers-as-Images"
    class="minibutton with-count js-toggler-target star-button tooltipped tooltipped-n"
    aria-label="You must be signed in to star a repository" rel="nofollow">
    <span class="octicon octicon-star"></span>Star
  </a>

    <a class="social-count js-social-count" href="/jwa107/Photoshop-Export-Layers-as-Images/stargazers">
      49
    </a>

  </li>

    <li>
      <a href="/login?return_to=%2Fjwa107%2FPhotoshop-Export-Layers-as-Images"
        class="minibutton with-count js-toggler-target fork-button tooltipped tooltipped-n"
        aria-label="You must be signed in to fork a repository" rel="nofollow">
        <span class="octicon octicon-git-branch"></span>Fork
      </a>
      <a href="/jwa107/Photoshop-Export-Layers-as-Images/network" class="social-count">
        32
      </a>
    </li>
</ul>

        <h1 itemscope itemtype="http://data-vocabulary.org/Breadcrumb" class="entry-title public">
          <span class="repo-label"><span>public</span></span>
          <span class="mega-octicon octicon-repo"></span>
          <span class="author">
            <a href="/jwa107" class="url fn" itemprop="url" rel="author"><span itemprop="title">jwa107</span></a>
          </span>
          <span class="repohead-name-divider">/</span>
          <strong><a href="/jwa107/Photoshop-Export-Layers-as-Images" class="js-current-repository js-repo-home-link">Photoshop-Export-Layers-as-Images</a></strong>

          <span class="page-context-loader">
            <img alt="Octocat-spinner-32" height="16" src="https://github.global.ssl.fastly.net/images/spinners/octocat-spinner-32.gif" width="16" />
          </span>

        </h1>
      </div><!-- /.container -->
    </div><!-- /.repohead -->

    <div class="container">
      <div class="repository-with-sidebar repo-container new-discussion-timeline js-new-discussion-timeline  ">
        <div class="repository-sidebar clearfix">
            

<div class="sunken-menu vertical-right repo-nav js-repo-nav js-repository-container-pjax js-octicon-loaders">
  <div class="sunken-menu-contents">
    <ul class="sunken-menu-group">
      <li class="tooltipped tooltipped-w" aria-label="Code">
        <a href="/jwa107/Photoshop-Export-Layers-as-Images" aria-label="Code" class="selected js-selected-navigation-item sunken-menu-item" data-gotokey="c" data-pjax="true" data-selected-links="repo_source repo_downloads repo_commits repo_tags repo_branches /jwa107/Photoshop-Export-Layers-as-Images">
          <span class="octicon octicon-code"></span> <span class="full-word">Code</span>
          <img alt="Octocat-spinner-32" class="mini-loader" height="16" src="https://github.global.ssl.fastly.net/images/spinners/octocat-spinner-32.gif" width="16" />
</a>      </li>

        <li class="tooltipped tooltipped-w" aria-label="Issues">
          <a href="/jwa107/Photoshop-Export-Layers-as-Images/issues" aria-label="Issues" class="js-selected-navigation-item sunken-menu-item js-disable-pjax" data-gotokey="i" data-selected-links="repo_issues /jwa107/Photoshop-Export-Layers-as-Images/issues">
            <span class="octicon octicon-issue-opened"></span> <span class="full-word">Issues</span>
            <span class='counter'>2</span>
            <img alt="Octocat-spinner-32" class="mini-loader" height="16" src="https://github.global.ssl.fastly.net/images/spinners/octocat-spinner-32.gif" width="16" />
</a>        </li>

      <li class="tooltipped tooltipped-w" aria-label="Pull Requests">
        <a href="/jwa107/Photoshop-Export-Layers-as-Images/pulls" aria-label="Pull Requests" class="js-selected-navigation-item sunken-menu-item js-disable-pjax" data-gotokey="p" data-selected-links="repo_pulls /jwa107/Photoshop-Export-Layers-as-Images/pulls">
            <span class="octicon octicon-git-pull-request"></span> <span class="full-word">Pull Requests</span>
            <span class='counter'>1</span>
            <img alt="Octocat-spinner-32" class="mini-loader" height="16" src="https://github.global.ssl.fastly.net/images/spinners/octocat-spinner-32.gif" width="16" />
</a>      </li>


    </ul>
    <div class="sunken-menu-separator"></div>
    <ul class="sunken-menu-group">

      <li class="tooltipped tooltipped-w" aria-label="Pulse">
        <a href="/jwa107/Photoshop-Export-Layers-as-Images/pulse" aria-label="Pulse" class="js-selected-navigation-item sunken-menu-item" data-pjax="true" data-selected-links="pulse /jwa107/Photoshop-Export-Layers-as-Images/pulse">
          <span class="octicon octicon-pulse"></span> <span class="full-word">Pulse</span>
          <img alt="Octocat-spinner-32" class="mini-loader" height="16" src="https://github.global.ssl.fastly.net/images/spinners/octocat-spinner-32.gif" width="16" />
</a>      </li>

      <li class="tooltipped tooltipped-w" aria-label="Graphs">
        <a href="/jwa107/Photoshop-Export-Layers-as-Images/graphs" aria-label="Graphs" class="js-selected-navigation-item sunken-menu-item" data-pjax="true" data-selected-links="repo_graphs repo_contributors /jwa107/Photoshop-Export-Layers-as-Images/graphs">
          <span class="octicon octicon-graph"></span> <span class="full-word">Graphs</span>
          <img alt="Octocat-spinner-32" class="mini-loader" height="16" src="https://github.global.ssl.fastly.net/images/spinners/octocat-spinner-32.gif" width="16" />
</a>      </li>

      <li class="tooltipped tooltipped-w" aria-label="Network">
        <a href="/jwa107/Photoshop-Export-Layers-as-Images/network" aria-label="Network" class="js-selected-navigation-item sunken-menu-item js-disable-pjax" data-selected-links="repo_network /jwa107/Photoshop-Export-Layers-as-Images/network">
          <span class="octicon octicon-git-branch"></span> <span class="full-word">Network</span>
          <img alt="Octocat-spinner-32" class="mini-loader" height="16" src="https://github.global.ssl.fastly.net/images/spinners/octocat-spinner-32.gif" width="16" />
</a>      </li>
    </ul>


  </div>
</div>

              <div class="only-with-full-nav">
                

  

<div class="clone-url open"
  data-protocol-type="http"
  data-url="/users/set_protocol?protocol_selector=http&amp;protocol_type=clone">
  <h3><strong>HTTPS</strong> clone URL</h3>
  <div class="clone-url-box">
    <input type="text" class="clone js-url-field"
           value="https://github.com/jwa107/Photoshop-Export-Layers-as-Images.git" readonly="readonly">

    <span aria-label="copy to clipboard" class="js-zeroclipboard url-box-clippy minibutton zeroclipboard-button" data-clipboard-text="https://github.com/jwa107/Photoshop-Export-Layers-as-Images.git" data-copied-hint="copied!"><span class="octicon octicon-clippy"></span></span>
  </div>
</div>

  

<div class="clone-url "
  data-protocol-type="subversion"
  data-url="/users/set_protocol?protocol_selector=subversion&amp;protocol_type=clone">
  <h3><strong>Subversion</strong> checkout URL</h3>
  <div class="clone-url-box">
    <input type="text" class="clone js-url-field"
           value="https://github.com/jwa107/Photoshop-Export-Layers-as-Images" readonly="readonly">

    <span aria-label="copy to clipboard" class="js-zeroclipboard url-box-clippy minibutton zeroclipboard-button" data-clipboard-text="https://github.com/jwa107/Photoshop-Export-Layers-as-Images" data-copied-hint="copied!"><span class="octicon octicon-clippy"></span></span>
  </div>
</div>


<p class="clone-options">You can clone with
      <a href="#" class="js-clone-selector" data-protocol="http">HTTPS</a>
      or <a href="#" class="js-clone-selector" data-protocol="subversion">Subversion</a>.
  <span class="help tooltipped tooltipped-n" aria-label="Get help on which URL is right for you.">
    <a href="https://help.github.com/articles/which-remote-url-should-i-use">
    <span class="octicon octicon-question"></span>
    </a>
  </span>
</p>


  <a href="http://windows.github.com" class="minibutton sidebar-button" title="Save jwa107/Photoshop-Export-Layers-as-Images to your computer and use it in GitHub Desktop." aria-label="Save jwa107/Photoshop-Export-Layers-as-Images to your computer and use it in GitHub Desktop.">
    <span class="octicon octicon-device-desktop"></span>
    Clone in Desktop
  </a>

                <a href="/jwa107/Photoshop-Export-Layers-as-Images/archive/master.zip"
                   class="minibutton sidebar-button"
                   aria-label="Download jwa107/Photoshop-Export-Layers-as-Images as a zip file"
                   title="Download jwa107/Photoshop-Export-Layers-as-Images as a zip file"
                   rel="nofollow">
                  <span class="octicon octicon-cloud-download"></span>
                  Download ZIP
                </a>
              </div>
        </div><!-- /.repository-sidebar -->

        <div id="js-repo-pjax-container" class="repository-content context-loader-container" data-pjax-container>
          


<!-- blob contrib key: blob_contributors:v21:92b664c720556a053f9309f107237123 -->

<p title="This is a placeholder element" class="js-history-link-replace hidden"></p>

<a href="/jwa107/Photoshop-Export-Layers-as-Images/find/master" data-pjax data-hotkey="t" class="js-show-file-finder" style="display:none">Show File Finder</a>

<div class="file-navigation">
  

<div class="select-menu js-menu-container js-select-menu" >
  <span class="minibutton select-menu-button js-menu-target" data-hotkey="w"
    data-master-branch="master"
    data-ref="master"
    role="button" aria-label="Switch branches or tags" tabindex="0" aria-haspopup="true">
    <span class="octicon octicon-git-branch"></span>
    <i>branch:</i>
    <span class="js-select-button">master</span>
  </span>

  <div class="select-menu-modal-holder js-menu-content js-navigation-container" data-pjax aria-hidden="true">

    <div class="select-menu-modal">
      <div class="select-menu-header">
        <span class="select-menu-title">Switch branches/tags</span>
        <span class="octicon octicon-remove-close js-menu-close"></span>
      </div> <!-- /.select-menu-header -->

      <div class="select-menu-filters">
        <div class="select-menu-text-filter">
          <input type="text" aria-label="Filter branches/tags" id="context-commitish-filter-field" class="js-filterable-field js-navigation-enable" placeholder="Filter branches/tags">
        </div>
        <div class="select-menu-tabs">
          <ul>
            <li class="select-menu-tab">
              <a href="#" data-tab-filter="branches" class="js-select-menu-tab">Branches</a>
            </li>
            <li class="select-menu-tab">
              <a href="#" data-tab-filter="tags" class="js-select-menu-tab">Tags</a>
            </li>
          </ul>
        </div><!-- /.select-menu-tabs -->
      </div><!-- /.select-menu-filters -->

      <div class="select-menu-list select-menu-tab-bucket js-select-menu-tab-bucket" data-tab-filter="branches">

        <div data-filterable-for="context-commitish-filter-field" data-filterable-type="substring">


            <div class="select-menu-item js-navigation-item selected">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <a href="/jwa107/Photoshop-Export-Layers-as-Images/blob/master/SaveLayers.jsx"
                 data-name="master"
                 data-skip-pjax="true"
                 rel="nofollow"
                 class="js-navigation-open select-menu-item-text js-select-button-text css-truncate-target"
                 title="master">master</a>
            </div> <!-- /.select-menu-item -->
        </div>

          <div class="select-menu-no-results">Nothing to show</div>
      </div> <!-- /.select-menu-list -->

      <div class="select-menu-list select-menu-tab-bucket js-select-menu-tab-bucket" data-tab-filter="tags">
        <div data-filterable-for="context-commitish-filter-field" data-filterable-type="substring">


        </div>

        <div class="select-menu-no-results">Nothing to show</div>
      </div> <!-- /.select-menu-list -->

    </div> <!-- /.select-menu-modal -->
  </div> <!-- /.select-menu-modal-holder -->
</div> <!-- /.select-menu -->

  <div class="breadcrumb">
    <span class='repo-root js-repo-root'><span itemscope="" itemtype="http://data-vocabulary.org/Breadcrumb"><a href="/jwa107/Photoshop-Export-Layers-as-Images" data-branch="master" data-direction="back" data-pjax="true" itemscope="url"><span itemprop="title">Photoshop-Export-Layers-as-Images</span></a></span></span><span class="separator"> / </span><strong class="final-path">SaveLayers.jsx</strong> <span aria-label="copy to clipboard" class="js-zeroclipboard minibutton zeroclipboard-button" data-clipboard-text="SaveLayers.jsx" data-copied-hint="copied!"><span class="octicon octicon-clippy"></span></span>
  </div>
</div>


  <div class="commit file-history-tease">
    <img alt="yushiro" class="main-avatar js-avatar" data-user="415386" height="24" src="https://1.gravatar.com/avatar/7fcb260274ac4611df60f44e7ba07203?d=https%3A%2F%2Fidenticons.github.com%2F1ebb25a51e3e010f78122ff970b7477a.png&amp;r=x&amp;s=140" width="24" />
    <span class="author"><a href="/yushiro" rel="author">yushiro</a></span>
    <time class="js-relative-date" data-title-format="YYYY-MM-DD HH:mm:ss" datetime="2014-02-12T00:54:42-08:00" title="2014-02-12 00:54:42">February 12, 2014</time>
    <div class="commit-title">
        <a href="/jwa107/Photoshop-Export-Layers-as-Images/commit/784315acc88f037999cdbe0a5b5c6a4a2905091c" class="message" data-pjax="true" title="replace invaild characters in filename at windows">replace invaild characters in filename at windows</a>
    </div>

    <div class="participation">
      <p class="quickstat"><a href="#blob_contributors_box" rel="facebox"><strong>2</strong> contributors</a></p>
          <a class="avatar tooltipped tooltipped-s" aria-label="Tangleworm" href="/jwa107/Photoshop-Export-Layers-as-Images/commits/master/SaveLayers.jsx?author=Tangleworm"><img alt="Justin Wang" class=" js-avatar" data-user="5415736" height="20" src="https://2.gravatar.com/avatar/1e913947f8860b7dcee5c4382367c23e?d=https%3A%2F%2Fidenticons.github.com%2F8190c8ce526012f83f7131398bea0751.png&amp;r=x&amp;s=140" width="20" /></a>
    <a class="avatar tooltipped tooltipped-s" aria-label="yushiro" href="/jwa107/Photoshop-Export-Layers-as-Images/commits/master/SaveLayers.jsx?author=yushiro"><img alt="yushiro" class=" js-avatar" data-user="415386" height="20" src="https://1.gravatar.com/avatar/7fcb260274ac4611df60f44e7ba07203?d=https%3A%2F%2Fidenticons.github.com%2F1ebb25a51e3e010f78122ff970b7477a.png&amp;r=x&amp;s=140" width="20" /></a>


    </div>
    <div id="blob_contributors_box" style="display:none">
      <h2 class="facebox-header">Users who have contributed to this file</h2>
      <ul class="facebox-user-list">
          <li class="facebox-user-list-item">
            <img alt="Justin Wang" class=" js-avatar" data-user="5415736" height="24" src="https://2.gravatar.com/avatar/1e913947f8860b7dcee5c4382367c23e?d=https%3A%2F%2Fidenticons.github.com%2F8190c8ce526012f83f7131398bea0751.png&amp;r=x&amp;s=140" width="24" />
            <a href="/Tangleworm">Tangleworm</a>
          </li>
          <li class="facebox-user-list-item">
            <img alt="yushiro" class=" js-avatar" data-user="415386" height="24" src="https://1.gravatar.com/avatar/7fcb260274ac4611df60f44e7ba07203?d=https%3A%2F%2Fidenticons.github.com%2F1ebb25a51e3e010f78122ff970b7477a.png&amp;r=x&amp;s=140" width="24" />
            <a href="/yushiro">yushiro</a>
          </li>
      </ul>
    </div>
  </div>

<div class="file-box">
  <div class="file">
    <div class="meta clearfix">
      <div class="info file-name">
        <span class="icon"><b class="octicon octicon-file-text"></b></span>
        <span class="mode" title="File Mode">file</span>
        <span class="meta-divider"></span>
          <span>228 lines (191 sloc)</span>
          <span class="meta-divider"></span>
        <span>6.512 kb</span>
      </div>
      <div class="actions">
        <div class="button-group">
            <a class="minibutton tooltipped tooltipped-w"
               href="http://windows.github.com" aria-label="Open this file in GitHub for Windows">
                <span class="octicon octicon-device-desktop"></span> Open
            </a>
              <a class="minibutton disabled tooltipped tooltipped-w" href="#"
                 aria-label="You must be signed in to make or propose changes">Edit</a>
          <a href="/jwa107/Photoshop-Export-Layers-as-Images/raw/master/SaveLayers.jsx" class="button minibutton " id="raw-url">Raw</a>
            <a href="/jwa107/Photoshop-Export-Layers-as-Images/blame/master/SaveLayers.jsx" class="button minibutton js-update-url-with-hash">Blame</a>
          <a href="/jwa107/Photoshop-Export-Layers-as-Images/commits/master/SaveLayers.jsx" class="button minibutton " rel="nofollow">History</a>
        </div><!-- /.button-group -->
          <a class="minibutton danger disabled empty-icon tooltipped tooltipped-w" href="#"
             aria-label="You must be signed in to make or propose changes">
          Delete
        </a>
      </div><!-- /.actions -->
    </div>
        <div class="blob-wrapper data type-javascript js-blob-data">
        <table class="file-code file-diff tab-size-8">
          <tr class="file-code-line">
            <td class="blob-line-nums">
              <span id="L1" rel="#L1">1</span>
<span id="L2" rel="#L2">2</span>
<span id="L3" rel="#L3">3</span>
<span id="L4" rel="#L4">4</span>
<span id="L5" rel="#L5">5</span>
<span id="L6" rel="#L6">6</span>
<span id="L7" rel="#L7">7</span>
<span id="L8" rel="#L8">8</span>
<span id="L9" rel="#L9">9</span>
<span id="L10" rel="#L10">10</span>
<span id="L11" rel="#L11">11</span>
<span id="L12" rel="#L12">12</span>
<span id="L13" rel="#L13">13</span>
<span id="L14" rel="#L14">14</span>
<span id="L15" rel="#L15">15</span>
<span id="L16" rel="#L16">16</span>
<span id="L17" rel="#L17">17</span>
<span id="L18" rel="#L18">18</span>
<span id="L19" rel="#L19">19</span>
<span id="L20" rel="#L20">20</span>
<span id="L21" rel="#L21">21</span>
<span id="L22" rel="#L22">22</span>
<span id="L23" rel="#L23">23</span>
<span id="L24" rel="#L24">24</span>
<span id="L25" rel="#L25">25</span>
<span id="L26" rel="#L26">26</span>
<span id="L27" rel="#L27">27</span>
<span id="L28" rel="#L28">28</span>
<span id="L29" rel="#L29">29</span>
<span id="L30" rel="#L30">30</span>
<span id="L31" rel="#L31">31</span>
<span id="L32" rel="#L32">32</span>
<span id="L33" rel="#L33">33</span>
<span id="L34" rel="#L34">34</span>
<span id="L35" rel="#L35">35</span>
<span id="L36" rel="#L36">36</span>
<span id="L37" rel="#L37">37</span>
<span id="L38" rel="#L38">38</span>
<span id="L39" rel="#L39">39</span>
<span id="L40" rel="#L40">40</span>
<span id="L41" rel="#L41">41</span>
<span id="L42" rel="#L42">42</span>
<span id="L43" rel="#L43">43</span>
<span id="L44" rel="#L44">44</span>
<span id="L45" rel="#L45">45</span>
<span id="L46" rel="#L46">46</span>
<span id="L47" rel="#L47">47</span>
<span id="L48" rel="#L48">48</span>
<span id="L49" rel="#L49">49</span>
<span id="L50" rel="#L50">50</span>
<span id="L51" rel="#L51">51</span>
<span id="L52" rel="#L52">52</span>
<span id="L53" rel="#L53">53</span>
<span id="L54" rel="#L54">54</span>
<span id="L55" rel="#L55">55</span>
<span id="L56" rel="#L56">56</span>
<span id="L57" rel="#L57">57</span>
<span id="L58" rel="#L58">58</span>
<span id="L59" rel="#L59">59</span>
<span id="L60" rel="#L60">60</span>
<span id="L61" rel="#L61">61</span>
<span id="L62" rel="#L62">62</span>
<span id="L63" rel="#L63">63</span>
<span id="L64" rel="#L64">64</span>
<span id="L65" rel="#L65">65</span>
<span id="L66" rel="#L66">66</span>
<span id="L67" rel="#L67">67</span>
<span id="L68" rel="#L68">68</span>
<span id="L69" rel="#L69">69</span>
<span id="L70" rel="#L70">70</span>
<span id="L71" rel="#L71">71</span>
<span id="L72" rel="#L72">72</span>
<span id="L73" rel="#L73">73</span>
<span id="L74" rel="#L74">74</span>
<span id="L75" rel="#L75">75</span>
<span id="L76" rel="#L76">76</span>
<span id="L77" rel="#L77">77</span>
<span id="L78" rel="#L78">78</span>
<span id="L79" rel="#L79">79</span>
<span id="L80" rel="#L80">80</span>
<span id="L81" rel="#L81">81</span>
<span id="L82" rel="#L82">82</span>
<span id="L83" rel="#L83">83</span>
<span id="L84" rel="#L84">84</span>
<span id="L85" rel="#L85">85</span>
<span id="L86" rel="#L86">86</span>
<span id="L87" rel="#L87">87</span>
<span id="L88" rel="#L88">88</span>
<span id="L89" rel="#L89">89</span>
<span id="L90" rel="#L90">90</span>
<span id="L91" rel="#L91">91</span>
<span id="L92" rel="#L92">92</span>
<span id="L93" rel="#L93">93</span>
<span id="L94" rel="#L94">94</span>
<span id="L95" rel="#L95">95</span>
<span id="L96" rel="#L96">96</span>
<span id="L97" rel="#L97">97</span>
<span id="L98" rel="#L98">98</span>
<span id="L99" rel="#L99">99</span>
<span id="L100" rel="#L100">100</span>
<span id="L101" rel="#L101">101</span>
<span id="L102" rel="#L102">102</span>
<span id="L103" rel="#L103">103</span>
<span id="L104" rel="#L104">104</span>
<span id="L105" rel="#L105">105</span>
<span id="L106" rel="#L106">106</span>
<span id="L107" rel="#L107">107</span>
<span id="L108" rel="#L108">108</span>
<span id="L109" rel="#L109">109</span>
<span id="L110" rel="#L110">110</span>
<span id="L111" rel="#L111">111</span>
<span id="L112" rel="#L112">112</span>
<span id="L113" rel="#L113">113</span>
<span id="L114" rel="#L114">114</span>
<span id="L115" rel="#L115">115</span>
<span id="L116" rel="#L116">116</span>
<span id="L117" rel="#L117">117</span>
<span id="L118" rel="#L118">118</span>
<span id="L119" rel="#L119">119</span>
<span id="L120" rel="#L120">120</span>
<span id="L121" rel="#L121">121</span>
<span id="L122" rel="#L122">122</span>
<span id="L123" rel="#L123">123</span>
<span id="L124" rel="#L124">124</span>
<span id="L125" rel="#L125">125</span>
<span id="L126" rel="#L126">126</span>
<span id="L127" rel="#L127">127</span>
<span id="L128" rel="#L128">128</span>
<span id="L129" rel="#L129">129</span>
<span id="L130" rel="#L130">130</span>
<span id="L131" rel="#L131">131</span>
<span id="L132" rel="#L132">132</span>
<span id="L133" rel="#L133">133</span>
<span id="L134" rel="#L134">134</span>
<span id="L135" rel="#L135">135</span>
<span id="L136" rel="#L136">136</span>
<span id="L137" rel="#L137">137</span>
<span id="L138" rel="#L138">138</span>
<span id="L139" rel="#L139">139</span>
<span id="L140" rel="#L140">140</span>
<span id="L141" rel="#L141">141</span>
<span id="L142" rel="#L142">142</span>
<span id="L143" rel="#L143">143</span>
<span id="L144" rel="#L144">144</span>
<span id="L145" rel="#L145">145</span>
<span id="L146" rel="#L146">146</span>
<span id="L147" rel="#L147">147</span>
<span id="L148" rel="#L148">148</span>
<span id="L149" rel="#L149">149</span>
<span id="L150" rel="#L150">150</span>
<span id="L151" rel="#L151">151</span>
<span id="L152" rel="#L152">152</span>
<span id="L153" rel="#L153">153</span>
<span id="L154" rel="#L154">154</span>
<span id="L155" rel="#L155">155</span>
<span id="L156" rel="#L156">156</span>
<span id="L157" rel="#L157">157</span>
<span id="L158" rel="#L158">158</span>
<span id="L159" rel="#L159">159</span>
<span id="L160" rel="#L160">160</span>
<span id="L161" rel="#L161">161</span>
<span id="L162" rel="#L162">162</span>
<span id="L163" rel="#L163">163</span>
<span id="L164" rel="#L164">164</span>
<span id="L165" rel="#L165">165</span>
<span id="L166" rel="#L166">166</span>
<span id="L167" rel="#L167">167</span>
<span id="L168" rel="#L168">168</span>
<span id="L169" rel="#L169">169</span>
<span id="L170" rel="#L170">170</span>
<span id="L171" rel="#L171">171</span>
<span id="L172" rel="#L172">172</span>
<span id="L173" rel="#L173">173</span>
<span id="L174" rel="#L174">174</span>
<span id="L175" rel="#L175">175</span>
<span id="L176" rel="#L176">176</span>
<span id="L177" rel="#L177">177</span>
<span id="L178" rel="#L178">178</span>
<span id="L179" rel="#L179">179</span>
<span id="L180" rel="#L180">180</span>
<span id="L181" rel="#L181">181</span>
<span id="L182" rel="#L182">182</span>
<span id="L183" rel="#L183">183</span>
<span id="L184" rel="#L184">184</span>
<span id="L185" rel="#L185">185</span>
<span id="L186" rel="#L186">186</span>
<span id="L187" rel="#L187">187</span>
<span id="L188" rel="#L188">188</span>
<span id="L189" rel="#L189">189</span>
<span id="L190" rel="#L190">190</span>
<span id="L191" rel="#L191">191</span>
<span id="L192" rel="#L192">192</span>
<span id="L193" rel="#L193">193</span>
<span id="L194" rel="#L194">194</span>
<span id="L195" rel="#L195">195</span>
<span id="L196" rel="#L196">196</span>
<span id="L197" rel="#L197">197</span>
<span id="L198" rel="#L198">198</span>
<span id="L199" rel="#L199">199</span>
<span id="L200" rel="#L200">200</span>
<span id="L201" rel="#L201">201</span>
<span id="L202" rel="#L202">202</span>
<span id="L203" rel="#L203">203</span>
<span id="L204" rel="#L204">204</span>
<span id="L205" rel="#L205">205</span>
<span id="L206" rel="#L206">206</span>
<span id="L207" rel="#L207">207</span>
<span id="L208" rel="#L208">208</span>
<span id="L209" rel="#L209">209</span>
<span id="L210" rel="#L210">210</span>
<span id="L211" rel="#L211">211</span>
<span id="L212" rel="#L212">212</span>
<span id="L213" rel="#L213">213</span>
<span id="L214" rel="#L214">214</span>
<span id="L215" rel="#L215">215</span>
<span id="L216" rel="#L216">216</span>
<span id="L217" rel="#L217">217</span>
<span id="L218" rel="#L218">218</span>
<span id="L219" rel="#L219">219</span>
<span id="L220" rel="#L220">220</span>
<span id="L221" rel="#L221">221</span>
<span id="L222" rel="#L222">222</span>
<span id="L223" rel="#L223">223</span>
<span id="L224" rel="#L224">224</span>
<span id="L225" rel="#L225">225</span>
<span id="L226" rel="#L226">226</span>
<span id="L227" rel="#L227">227</span>

            </td>
            <td class="blob-line-code"><div class="code-body highlight"><pre><div class='line' id='LC1'><span class="c1">// NAME: </span></div><div class='line' id='LC2'><span class="c1">// 	SaveLayers</span></div><div class='line' id='LC3'><br/></div><div class='line' id='LC4'><span class="c1">// DESCRIPTION: </span></div><div class='line' id='LC5'><span class="c1">//	Saves each layer in the active document to a PNG or JPG file named after the layer. </span></div><div class='line' id='LC6'><span class="c1">//	These files will be created in the current document folder (same as working PSD).</span></div><div class='line' id='LC7'><br/></div><div class='line' id='LC8'><span class="c1">// REQUIRES: </span></div><div class='line' id='LC9'><span class="c1">// 	Adobe Photoshop CS2 or higher</span></div><div class='line' id='LC10'><br/></div><div class='line' id='LC11'><span class="c1">//Most current version always available at: https://github.com/jwa107/Photoshop-Export-Layers-as-Images</span></div><div class='line' id='LC12'><br/></div><div class='line' id='LC13'><span class="c1">// enable double-clicking from Finder/Explorer (CS2 and higher)</span></div><div class='line' id='LC14'><span class="err">#</span><span class="nx">target</span> <span class="nx">photoshop</span></div><div class='line' id='LC15'><span class="nx">app</span><span class="p">.</span><span class="nx">bringToFront</span><span class="p">();</span></div><div class='line' id='LC16'><br/></div><div class='line' id='LC17'><span class="kd">function</span> <span class="nx">main</span><span class="p">()</span> <span class="p">{</span></div><div class='line' id='LC18'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// two quick checks</span></div><div class='line' id='LC19'>	<span class="k">if</span><span class="p">(</span><span class="o">!</span><span class="nx">okDocument</span><span class="p">())</span> <span class="p">{</span></div><div class='line' id='LC20'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">alert</span><span class="p">(</span><span class="s2">&quot;Document must be saved and be a layered PSD.&quot;</span><span class="p">);</span></div><div class='line' id='LC21'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span><span class="p">;</span> </div><div class='line' id='LC22'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC23'>&nbsp;&nbsp;&nbsp;&nbsp;</div><div class='line' id='LC24'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">len</span> <span class="o">=</span> <span class="nx">activeDocument</span><span class="p">.</span><span class="nx">layers</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span></div><div class='line' id='LC25'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">ok</span> <span class="o">=</span> <span class="nx">confirm</span><span class="p">(</span><span class="s2">&quot;Note: All layers will be saved in same directory as your PSD.\nThis document contains &quot;</span> <span class="o">+</span> <span class="nx">len</span> <span class="o">+</span> <span class="s2">&quot; top level layers.\nBe aware that large numbers of layers may take some time!\nContinue?&quot;</span><span class="p">);</span></div><div class='line' id='LC26'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span><span class="p">(</span><span class="o">!</span><span class="nx">ok</span><span class="p">)</span> <span class="k">return</span></div><div class='line' id='LC27'><br/></div><div class='line' id='LC28'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// user preferences</span></div><div class='line' id='LC29'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">prefs</span> <span class="o">=</span> <span class="k">new</span> <span class="nb">Object</span><span class="p">();</span></div><div class='line' id='LC30'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">prefs</span><span class="p">.</span><span class="nx">fileType</span> <span class="o">=</span> <span class="s2">&quot;&quot;</span><span class="p">;</span></div><div class='line' id='LC31'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">prefs</span><span class="p">.</span><span class="nx">fileQuality</span> <span class="o">=</span> <span class="mi">12</span><span class="p">;</span></div><div class='line' id='LC32'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">prefs</span><span class="p">.</span><span class="nx">filePath</span> <span class="o">=</span> <span class="nx">app</span><span class="p">.</span><span class="nx">activeDocument</span><span class="p">.</span><span class="nx">path</span><span class="p">;</span></div><div class='line' id='LC33'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">prefs</span><span class="p">.</span><span class="nx">count</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span></div><div class='line' id='LC34'><br/></div><div class='line' id='LC35'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">//instantiate dialogue</span></div><div class='line' id='LC36'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">Dialog</span><span class="p">();</span></div><div class='line' id='LC37'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">hideLayers</span><span class="p">(</span><span class="nx">activeDocument</span><span class="p">);</span></div><div class='line' id='LC38'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">saveLayers</span><span class="p">(</span><span class="nx">activeDocument</span><span class="p">);</span></div><div class='line' id='LC39'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">toggleVisibility</span><span class="p">(</span><span class="nx">activeDocument</span><span class="p">);</span></div><div class='line' id='LC40'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">alert</span><span class="p">(</span><span class="s2">&quot;Saved &quot;</span> <span class="o">+</span> <span class="nx">prefs</span><span class="p">.</span><span class="nx">count</span> <span class="o">+</span> <span class="s2">&quot; files.&quot;</span><span class="p">);</span></div><div class='line' id='LC41'><span class="p">}</span></div><div class='line' id='LC42'><br/></div><div class='line' id='LC43'><span class="kd">function</span> <span class="nx">hideLayers</span><span class="p">(</span><span class="nx">ref</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC44'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">len</span> <span class="o">=</span> <span class="nx">ref</span><span class="p">.</span><span class="nx">layers</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span></div><div class='line' id='LC45'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">for</span> <span class="p">(</span><span class="kd">var</span> <span class="nx">i</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span> <span class="nx">i</span> <span class="o">&lt;</span> <span class="nx">len</span><span class="p">;</span> <span class="nx">i</span><span class="o">++</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC46'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">layer</span> <span class="o">=</span> <span class="nx">ref</span><span class="p">.</span><span class="nx">layers</span><span class="p">[</span><span class="nx">i</span><span class="p">];</span></div><div class='line' id='LC47'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">layer</span><span class="p">.</span><span class="nx">typename</span> <span class="o">==</span> <span class="s1">&#39;LayerSet&#39;</span><span class="p">)</span> <span class="nx">hideLayers</span><span class="p">(</span><span class="nx">layer</span><span class="p">);</span></div><div class='line' id='LC48'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">else</span> <span class="nx">layer</span><span class="p">.</span><span class="nx">visible</span> <span class="o">=</span> <span class="kc">false</span><span class="p">;</span></div><div class='line' id='LC49'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC50'><span class="p">}</span></div><div class='line' id='LC51'><br/></div><div class='line' id='LC52'><span class="kd">function</span> <span class="nx">toggleVisibility</span><span class="p">(</span><span class="nx">ref</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC53'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">len</span> <span class="o">=</span> <span class="nx">ref</span><span class="p">.</span><span class="nx">layers</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span></div><div class='line' id='LC54'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">for</span> <span class="p">(</span><span class="kd">var</span> <span class="nx">i</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span> <span class="nx">i</span> <span class="o">&lt;</span> <span class="nx">len</span><span class="p">;</span> <span class="nx">i</span><span class="o">++</span><span class="p">)</span> <span class="p">{</span>	</div><div class='line' id='LC55'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">layer</span> <span class="o">=</span> <span class="nx">ref</span><span class="p">.</span><span class="nx">layers</span><span class="p">[</span><span class="nx">i</span><span class="p">];</span></div><div class='line' id='LC56'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">layer</span><span class="p">.</span><span class="nx">visible</span> <span class="o">=</span> <span class="o">!</span><span class="nx">layer</span><span class="p">.</span><span class="nx">visible</span><span class="p">;</span></div><div class='line' id='LC57'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC58'><span class="p">}</span></div><div class='line' id='LC59'><br/></div><div class='line' id='LC60'><span class="kd">function</span> <span class="nx">saveLayers</span><span class="p">(</span><span class="nx">ref</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC61'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">len</span> <span class="o">=</span> <span class="nx">ref</span><span class="p">.</span><span class="nx">layers</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span></div><div class='line' id='LC62'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// rename layers top to bottom</span></div><div class='line' id='LC63'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">for</span> <span class="p">(</span><span class="kd">var</span> <span class="nx">i</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span> <span class="nx">i</span> <span class="o">&lt;</span> <span class="nx">len</span><span class="p">;</span> <span class="nx">i</span><span class="o">++</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC64'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">layer</span> <span class="o">=</span> <span class="nx">ref</span><span class="p">.</span><span class="nx">layers</span><span class="p">[</span><span class="nx">i</span><span class="p">];</span></div><div class='line' id='LC65'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">layer</span><span class="p">.</span><span class="nx">typename</span> <span class="o">==</span> <span class="s1">&#39;LayerSet&#39;</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC66'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// recurse if current layer is a group</span></div><div class='line' id='LC67'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">hideLayers</span><span class="p">(</span><span class="nx">layer</span><span class="p">);</span></div><div class='line' id='LC68'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">saveLayers</span><span class="p">(</span><span class="nx">layer</span><span class="p">);</span></div><div class='line' id='LC69'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC70'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// otherwise make sure the layer is visible and save it</span></div><div class='line' id='LC71'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">layer</span><span class="p">.</span><span class="nx">visible</span> <span class="o">=</span> <span class="kc">true</span><span class="p">;</span></div><div class='line' id='LC72'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">saveImage</span><span class="p">(</span><span class="nx">layer</span><span class="p">.</span><span class="nx">name</span><span class="p">);</span></div><div class='line' id='LC73'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">layer</span><span class="p">.</span><span class="nx">visible</span> <span class="o">=</span> <span class="kc">false</span><span class="p">;</span></div><div class='line' id='LC74'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC75'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC76'><span class="p">}</span></div><div class='line' id='LC77'><br/></div><div class='line' id='LC78'><span class="kd">function</span> <span class="nx">saveImage</span><span class="p">(</span><span class="nx">layerName</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC79'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">fileName</span> <span class="o">=</span> <span class="nx">layerName</span><span class="p">.</span><span class="nx">replace</span><span class="p">(</span><span class="sr">/[\\\*\/\?:&quot;\|&lt;&gt; ]/g</span><span class="p">,</span><span class="s1">&#39;&#39;</span><span class="p">);</span> </div><div class='line' id='LC80'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span><span class="p">(</span><span class="nx">fileName</span><span class="p">.</span><span class="nx">length</span> <span class="o">==</span><span class="mi">0</span><span class="p">)</span> <span class="nx">fileName</span> <span class="o">=</span> <span class="s2">&quot;autoname&quot;</span><span class="p">;</span></div><div class='line' id='LC81'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">handle</span> <span class="o">=</span> <span class="nx">getUniqueName</span><span class="p">(</span><span class="nx">prefs</span><span class="p">.</span><span class="nx">filePath</span> <span class="o">+</span> <span class="s2">&quot;/&quot;</span> <span class="o">+</span> <span class="nx">fileName</span><span class="p">);</span></div><div class='line' id='LC82'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">prefs</span><span class="p">.</span><span class="nx">count</span><span class="o">++</span><span class="p">;</span></div><div class='line' id='LC83'>&nbsp;&nbsp;&nbsp;&nbsp;</div><div class='line' id='LC84'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span><span class="p">(</span><span class="nx">prefs</span><span class="p">.</span><span class="nx">fileType</span><span class="o">==</span><span class="s2">&quot;PNG&quot;</span> <span class="o">&amp;&amp;</span> <span class="nx">prefs</span><span class="p">.</span><span class="nx">fileQuality</span><span class="o">==</span><span class="s2">&quot;8&quot;</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC85'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">SavePNG8</span><span class="p">(</span><span class="nx">handle</span><span class="p">);</span> </div><div class='line' id='LC86'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="k">if</span> <span class="p">(</span><span class="nx">prefs</span><span class="p">.</span><span class="nx">fileType</span><span class="o">==</span><span class="s2">&quot;PNG&quot;</span> <span class="o">&amp;&amp;</span> <span class="nx">prefs</span><span class="p">.</span><span class="nx">fileQuality</span><span class="o">==</span><span class="s2">&quot;24&quot;</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC87'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">SavePNG24</span><span class="p">(</span><span class="nx">handle</span><span class="p">);</span></div><div class='line' id='LC88'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC89'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">SaveJPEG</span><span class="p">(</span><span class="nx">handle</span><span class="p">);</span> </div><div class='line' id='LC90'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC91'><span class="p">}</span></div><div class='line' id='LC92'><br/></div><div class='line' id='LC93'><span class="kd">function</span> <span class="nx">getUniqueName</span><span class="p">(</span><span class="nx">fileroot</span><span class="p">)</span> <span class="p">{</span> </div><div class='line' id='LC94'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// form a full file name</span></div><div class='line' id='LC95'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// if the file name exists, a numeric suffix will be added to disambiguate</span></div><div class='line' id='LC96'><br/></div><div class='line' id='LC97'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">filename</span> <span class="o">=</span> <span class="nx">fileroot</span><span class="p">;</span></div><div class='line' id='LC98'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">for</span> <span class="p">(</span><span class="kd">var</span> <span class="nx">i</span><span class="o">=</span><span class="mi">1</span><span class="p">;</span> <span class="nx">i</span><span class="o">&lt;</span><span class="mi">100</span><span class="p">;</span> <span class="nx">i</span><span class="o">++</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC99'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">handle</span> <span class="o">=</span> <span class="nx">File</span><span class="p">(</span><span class="nx">filename</span> <span class="o">+</span> <span class="s2">&quot;.&quot;</span> <span class="o">+</span> <span class="nx">prefs</span><span class="p">.</span><span class="nx">fileType</span><span class="p">);</span> </div><div class='line' id='LC100'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span><span class="p">(</span><span class="nx">handle</span><span class="p">.</span><span class="nx">exists</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC101'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">filename</span> <span class="o">=</span> <span class="nx">fileroot</span> <span class="o">+</span> <span class="s2">&quot;-&quot;</span> <span class="o">+</span> <span class="nx">padder</span><span class="p">(</span><span class="nx">i</span><span class="p">,</span> <span class="mi">3</span><span class="p">);</span></div><div class='line' id='LC102'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC103'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="nx">handle</span><span class="p">;</span> </div><div class='line' id='LC104'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC105'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC106'><span class="p">}</span> </div><div class='line' id='LC107'><br/></div><div class='line' id='LC108'><span class="kd">function</span> <span class="nx">padder</span><span class="p">(</span><span class="nx">input</span><span class="p">,</span> <span class="nx">padLength</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC109'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// pad the input with zeroes up to indicated length</span></div><div class='line' id='LC110'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">result</span> <span class="o">=</span> <span class="p">(</span><span class="k">new</span> <span class="nb">Array</span><span class="p">(</span><span class="nx">padLength</span> <span class="o">+</span> <span class="mi">1</span> <span class="o">-</span> <span class="nx">input</span><span class="p">.</span><span class="nx">toString</span><span class="p">().</span><span class="nx">length</span><span class="p">)).</span><span class="nx">join</span><span class="p">(</span><span class="s1">&#39;0&#39;</span><span class="p">)</span> <span class="o">+</span> <span class="nx">input</span><span class="p">;</span></div><div class='line' id='LC111'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="nx">result</span><span class="p">;</span></div><div class='line' id='LC112'><span class="p">}</span></div><div class='line' id='LC113'><br/></div><div class='line' id='LC114'><span class="kd">function</span> <span class="nx">SavePNG8</span><span class="p">(</span><span class="nx">saveFile</span><span class="p">)</span> <span class="p">{</span> </div><div class='line' id='LC115'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">exportOptionsSaveForWeb</span> <span class="o">=</span> <span class="k">new</span> <span class="nx">ExportOptionsSaveForWeb</span><span class="p">();</span></div><div class='line' id='LC116'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">exportOptionsSaveForWeb</span><span class="p">.</span><span class="nx">format</span> <span class="o">=</span> <span class="nx">SaveDocumentType</span><span class="p">.</span><span class="nx">PNG</span></div><div class='line' id='LC117'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">exportOptionsSaveForWeb</span><span class="p">.</span><span class="nx">dither</span> <span class="o">=</span> <span class="nx">Dither</span><span class="p">.</span><span class="nx">NONE</span><span class="p">;</span></div><div class='line' id='LC118'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">activeDocument</span><span class="p">.</span><span class="nx">exportDocument</span><span class="p">(</span> <span class="nx">saveFile</span><span class="p">,</span> <span class="nx">ExportType</span><span class="p">.</span><span class="nx">SAVEFORWEB</span><span class="p">,</span> <span class="nx">exportOptionsSaveForWeb</span> <span class="p">);</span></div><div class='line' id='LC119'><span class="p">}</span> </div><div class='line' id='LC120'><br/></div><div class='line' id='LC121'><span class="kd">function</span> <span class="nx">SavePNG24</span><span class="p">(</span><span class="nx">saveFile</span><span class="p">)</span> <span class="p">{</span> </div><div class='line' id='LC122'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">pngSaveOptions</span> <span class="o">=</span> <span class="k">new</span> <span class="nx">PNGSaveOptions</span><span class="p">();</span> </div><div class='line' id='LC123'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">activeDocument</span><span class="p">.</span><span class="nx">saveAs</span><span class="p">(</span><span class="nx">saveFile</span><span class="p">,</span> <span class="nx">pngSaveOptions</span><span class="p">,</span> <span class="kc">true</span><span class="p">,</span> <span class="nx">Extension</span><span class="p">.</span><span class="nx">LOWERCASE</span><span class="p">);</span> </div><div class='line' id='LC124'><span class="p">}</span> </div><div class='line' id='LC125'><br/></div><div class='line' id='LC126'><span class="kd">function</span> <span class="nx">SaveJPEG</span><span class="p">(</span><span class="nx">saveFile</span><span class="p">)</span> <span class="p">{</span> </div><div class='line' id='LC127'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">jpegSaveOptions</span> <span class="o">=</span> <span class="k">new</span> <span class="nx">JPEGSaveOptions</span><span class="p">();</span> </div><div class='line' id='LC128'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">jpegSaveOptions</span><span class="p">.</span><span class="nx">quality</span> <span class="o">=</span> <span class="nx">prefs</span><span class="p">.</span><span class="nx">fileQuality</span><span class="p">;</span></div><div class='line' id='LC129'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">activeDocument</span><span class="p">.</span><span class="nx">saveAs</span><span class="p">(</span><span class="nx">saveFile</span><span class="p">,</span> <span class="nx">jpegSaveOptions</span><span class="p">,</span> <span class="kc">true</span><span class="p">,</span> <span class="nx">Extension</span><span class="p">.</span><span class="nx">LOWERCASE</span><span class="p">);</span> </div><div class='line' id='LC130'><span class="p">}</span> </div><div class='line' id='LC131'><br/></div><div class='line' id='LC132'><span class="kd">function</span> <span class="nx">Dialog</span><span class="p">()</span> <span class="p">{</span></div><div class='line' id='LC133'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// build dialogue</span></div><div class='line' id='LC134'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">dlg</span> <span class="o">=</span> <span class="k">new</span> <span class="nx">Window</span> <span class="p">(</span><span class="s1">&#39;dialog&#39;</span><span class="p">,</span> <span class="s1">&#39;Select Type&#39;</span><span class="p">);</span> </div><div class='line' id='LC135'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">saver</span> <span class="o">=</span> <span class="nx">dlg</span><span class="p">.</span><span class="nx">add</span><span class="p">(</span><span class="s2">&quot;dropdownlist&quot;</span><span class="p">,</span> <span class="kc">undefined</span><span class="p">,</span> <span class="s2">&quot;&quot;</span><span class="p">);</span> </div><div class='line' id='LC136'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">quality</span> <span class="o">=</span> <span class="nx">dlg</span><span class="p">.</span><span class="nx">add</span><span class="p">(</span><span class="s2">&quot;dropdownlist&quot;</span><span class="p">,</span> <span class="kc">undefined</span><span class="p">,</span> <span class="s2">&quot;&quot;</span><span class="p">);</span></div><div class='line' id='LC137'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">pngtype</span> <span class="o">=</span> <span class="nx">dlg</span><span class="p">.</span><span class="nx">add</span><span class="p">(</span><span class="s2">&quot;dropdownlist&quot;</span><span class="p">,</span> <span class="kc">undefined</span><span class="p">,</span> <span class="s2">&quot;&quot;</span><span class="p">);</span></div><div class='line' id='LC138'><br/></div><div class='line' id='LC139'><br/></div><div class='line' id='LC140'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// file type</span></div><div class='line' id='LC141'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">saveOpt</span> <span class="o">=</span> <span class="p">[];</span></div><div class='line' id='LC142'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">saveOpt</span><span class="p">[</span><span class="mi">0</span><span class="p">]</span> <span class="o">=</span> <span class="s2">&quot;PNG&quot;</span><span class="p">;</span> </div><div class='line' id='LC143'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">saveOpt</span><span class="p">[</span><span class="mi">1</span><span class="p">]</span> <span class="o">=</span> <span class="s2">&quot;JPG&quot;</span><span class="p">;</span> </div><div class='line' id='LC144'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">for</span> <span class="p">(</span><span class="kd">var</span> <span class="nx">i</span><span class="o">=</span><span class="mi">0</span><span class="p">,</span> <span class="nx">len</span><span class="o">=</span><span class="nx">saveOpt</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span> <span class="nx">i</span><span class="o">&lt;</span><span class="nx">len</span><span class="p">;</span> <span class="nx">i</span><span class="o">++</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC145'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">saver</span><span class="p">.</span><span class="nx">add</span> <span class="p">(</span><span class="s2">&quot;item&quot;</span><span class="p">,</span> <span class="s2">&quot;Save as &quot;</span> <span class="o">+</span> <span class="nx">saveOpt</span><span class="p">[</span><span class="nx">i</span><span class="p">]);</span></div><div class='line' id='LC146'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">};</span> </div><div class='line' id='LC147'><br/></div><div class='line' id='LC148'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// trigger function</span></div><div class='line' id='LC149'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">saver</span><span class="p">.</span><span class="nx">onChange</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span></div><div class='line' id='LC150'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">prefs</span><span class="p">.</span><span class="nx">fileType</span> <span class="o">=</span> <span class="nx">saveOpt</span><span class="p">[</span><span class="nb">parseInt</span><span class="p">(</span><span class="k">this</span><span class="p">.</span><span class="nx">selection</span><span class="p">)];</span> </div><div class='line' id='LC151'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// decide whether to show JPG or PNG options</span></div><div class='line' id='LC152'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span><span class="p">(</span><span class="nx">prefs</span><span class="p">.</span><span class="nx">fileType</span><span class="o">==</span><span class="nx">saveOpt</span><span class="p">[</span><span class="mi">1</span><span class="p">]){</span></div><div class='line' id='LC153'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">quality</span><span class="p">.</span><span class="nx">show</span><span class="p">();</span></div><div class='line' id='LC154'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">pngtype</span><span class="p">.</span><span class="nx">hide</span><span class="p">();</span></div><div class='line' id='LC155'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC156'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">quality</span><span class="p">.</span><span class="nx">hide</span><span class="p">();</span></div><div class='line' id='LC157'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">pngtype</span><span class="p">.</span><span class="nx">show</span><span class="p">();</span></div><div class='line' id='LC158'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC159'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">};</span> </div><div class='line' id='LC160'><br/></div><div class='line' id='LC161'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// jpg quality</span></div><div class='line' id='LC162'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">qualityOpt</span> <span class="o">=</span> <span class="p">[];</span></div><div class='line' id='LC163'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">for</span><span class="p">(</span><span class="kd">var</span> <span class="nx">i</span><span class="o">=</span><span class="mi">12</span><span class="p">;</span> <span class="nx">i</span><span class="o">&gt;=</span><span class="mi">1</span><span class="p">;</span> <span class="nx">i</span><span class="o">--</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC164'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">qualityOpt</span><span class="p">[</span><span class="nx">i</span><span class="p">]</span> <span class="o">=</span> <span class="nx">i</span><span class="p">;</span></div><div class='line' id='LC165'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">quality</span><span class="p">.</span><span class="nx">add</span> <span class="p">(</span><span class="s1">&#39;item&#39;</span><span class="p">,</span> <span class="s2">&quot;&quot;</span> <span class="o">+</span> <span class="nx">i</span><span class="p">);</span></div><div class='line' id='LC166'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">};</span> </div><div class='line' id='LC167'><br/></div><div class='line' id='LC168'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// png type</span></div><div class='line' id='LC169'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">pngtypeOpt</span> <span class="o">=</span> <span class="p">[];</span></div><div class='line' id='LC170'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">pngtypeOpt</span><span class="p">[</span><span class="mi">0</span><span class="p">]</span><span class="o">=</span><span class="mi">8</span><span class="p">;</span></div><div class='line' id='LC171'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">pngtypeOpt</span><span class="p">[</span><span class="mi">1</span><span class="p">]</span><span class="o">=</span><span class="mi">24</span><span class="p">;</span></div><div class='line' id='LC172'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">pngtype</span><span class="p">.</span><span class="nx">add</span> <span class="p">(</span><span class="s1">&#39;item&#39;</span><span class="p">,</span> <span class="s2">&quot;&quot;</span><span class="o">+</span> <span class="mi">8</span> <span class="p">);</span></div><div class='line' id='LC173'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">pngtype</span><span class="p">.</span><span class="nx">add</span> <span class="p">(</span><span class="s1">&#39;item&#39;</span><span class="p">,</span> <span class="s2">&quot;&quot;</span> <span class="o">+</span> <span class="mi">24</span><span class="p">);</span></div><div class='line' id='LC174'><br/></div><div class='line' id='LC175'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// trigger functions</span></div><div class='line' id='LC176'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">quality</span><span class="p">.</span><span class="nx">onChange</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span></div><div class='line' id='LC177'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">prefs</span><span class="p">.</span><span class="nx">fileQuality</span> <span class="o">=</span> <span class="nx">qualityOpt</span><span class="p">[</span><span class="mi">12</span><span class="o">-</span><span class="nb">parseInt</span><span class="p">(</span><span class="k">this</span><span class="p">.</span><span class="nx">selection</span><span class="p">)];</span></div><div class='line' id='LC178'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">};</span></div><div class='line' id='LC179'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">pngtype</span><span class="p">.</span><span class="nx">onChange</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span></div><div class='line' id='LC180'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">prefs</span><span class="p">.</span><span class="nx">fileQuality</span> <span class="o">=</span> <span class="nx">pngtypeOpt</span><span class="p">[</span><span class="nb">parseInt</span><span class="p">(</span><span class="k">this</span><span class="p">.</span><span class="nx">selection</span><span class="p">)];</span> </div><div class='line' id='LC181'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">};</span></div><div class='line' id='LC182'><br/></div><div class='line' id='LC183'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// remainder of UI</span></div><div class='line' id='LC184'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">uiButtonRun</span> <span class="o">=</span> <span class="s2">&quot;Continue&quot;</span><span class="p">;</span> </div><div class='line' id='LC185'><br/></div><div class='line' id='LC186'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">btnRun</span> <span class="o">=</span> <span class="nx">dlg</span><span class="p">.</span><span class="nx">add</span><span class="p">(</span><span class="s2">&quot;button&quot;</span><span class="p">,</span> <span class="kc">undefined</span><span class="p">,</span> <span class="nx">uiButtonRun</span> <span class="p">);</span> </div><div class='line' id='LC187'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">btnRun</span><span class="p">.</span><span class="nx">onClick</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>	</div><div class='line' id='LC188'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">this</span><span class="p">.</span><span class="nx">parent</span><span class="p">.</span><span class="nx">close</span><span class="p">(</span><span class="mi">0</span><span class="p">);</span> </div><div class='line' id='LC189'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">};</span> </div><div class='line' id='LC190'><br/></div><div class='line' id='LC191'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">orientation</span> <span class="o">=</span> <span class="s1">&#39;column&#39;</span><span class="p">;</span> </div><div class='line' id='LC192'><br/></div><div class='line' id='LC193'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">saver</span><span class="p">.</span><span class="nx">selection</span> <span class="o">=</span> <span class="nx">dlg</span><span class="p">.</span><span class="nx">saver</span><span class="p">.</span><span class="nx">items</span><span class="p">[</span><span class="mi">0</span><span class="p">]</span> <span class="p">;</span></div><div class='line' id='LC194'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">quality</span><span class="p">.</span><span class="nx">selection</span> <span class="o">=</span> <span class="nx">dlg</span><span class="p">.</span><span class="nx">quality</span><span class="p">.</span><span class="nx">items</span><span class="p">[</span><span class="mi">0</span><span class="p">]</span> <span class="p">;</span></div><div class='line' id='LC195'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">center</span><span class="p">();</span> </div><div class='line' id='LC196'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">dlg</span><span class="p">.</span><span class="nx">show</span><span class="p">();</span></div><div class='line' id='LC197'><span class="p">}</span></div><div class='line' id='LC198'><br/></div><div class='line' id='LC199'><span class="kd">function</span> <span class="nx">okDocument</span><span class="p">()</span> <span class="p">{</span></div><div class='line' id='LC200'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// check that we have a valid document</span></div><div class='line' id='LC201'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div><div class='line' id='LC202'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">documents</span><span class="p">.</span><span class="nx">length</span><span class="p">)</span> <span class="k">return</span> <span class="kc">false</span><span class="p">;</span></div><div class='line' id='LC203'><br/></div><div class='line' id='LC204'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">thisDoc</span> <span class="o">=</span> <span class="nx">app</span><span class="p">.</span><span class="nx">activeDocument</span><span class="p">;</span> </div><div class='line' id='LC205'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">fileExt</span> <span class="o">=</span> <span class="nb">decodeURI</span><span class="p">(</span><span class="nx">thisDoc</span><span class="p">.</span><span class="nx">name</span><span class="p">).</span><span class="nx">replace</span><span class="p">(</span><span class="sr">/^.*\./</span><span class="p">,</span><span class="s1">&#39;&#39;</span><span class="p">);</span> </div><div class='line' id='LC206'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="nx">fileExt</span><span class="p">.</span><span class="nx">toLowerCase</span><span class="p">()</span> <span class="o">==</span> <span class="s1">&#39;psd&#39;</span></div><div class='line' id='LC207'><span class="p">}</span></div><div class='line' id='LC208'><br/></div><div class='line' id='LC209'><span class="kd">function</span> <span class="nx">wrapper</span><span class="p">()</span> <span class="p">{</span></div><div class='line' id='LC210'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">function</span> <span class="nx">showError</span><span class="p">(</span><span class="nx">err</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC211'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">alert</span><span class="p">(</span><span class="nx">err</span> <span class="o">+</span> <span class="s1">&#39;: on line &#39;</span> <span class="o">+</span> <span class="nx">err</span><span class="p">.</span><span class="nx">line</span><span class="p">,</span> <span class="s1">&#39;Script Error&#39;</span><span class="p">,</span> <span class="kc">true</span><span class="p">);</span></div><div class='line' id='LC212'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC213'><br/></div><div class='line' id='LC214'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">try</span> <span class="p">{</span></div><div class='line' id='LC215'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// suspend history for CS3 or higher</span></div><div class='line' id='LC216'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nb">parseInt</span><span class="p">(</span><span class="nx">version</span><span class="p">,</span> <span class="mi">10</span><span class="p">)</span> <span class="o">&gt;=</span> <span class="mi">10</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC217'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">activeDocument</span><span class="p">.</span><span class="nx">suspendHistory</span><span class="p">(</span><span class="s1">&#39;Save Layers&#39;</span><span class="p">,</span> <span class="s1">&#39;main()&#39;</span><span class="p">);</span></div><div class='line' id='LC218'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC219'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">main</span><span class="p">();</span></div><div class='line' id='LC220'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC221'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">catch</span><span class="p">(</span><span class="nx">e</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC222'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// report errors unless the user cancelled</span></div><div class='line' id='LC223'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">e</span><span class="p">.</span><span class="nx">number</span> <span class="o">!=</span> <span class="mi">8007</span><span class="p">)</span> <span class="nx">showError</span><span class="p">(</span><span class="nx">e</span><span class="p">);</span></div><div class='line' id='LC224'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC225'><span class="p">}</span></div><div class='line' id='LC226'><br/></div><div class='line' id='LC227'><span class="nx">wrapper</span><span class="p">();</span></div></pre></div></td>
          </tr>
        </table>
  </div>

  </div>
</div>

<a href="#jump-to-line" rel="facebox[.linejump]" data-hotkey="l" class="js-jump-to-line" style="display:none">Jump to Line</a>
<div id="jump-to-line" style="display:none">
  <form accept-charset="UTF-8" class="js-jump-to-line-form">
    <input class="linejump-input js-jump-to-line-field" type="text" placeholder="Jump to line&hellip;" autofocus>
    <button type="submit" class="button">Go</button>
  </form>
</div>

        </div>

      </div><!-- /.repo-container -->
      <div class="modal-backdrop"></div>
    </div><!-- /.container -->
  </div><!-- /.site -->


    </div><!-- /.wrapper -->

      <div class="container">
  <div class="site-footer">
    <ul class="site-footer-links right">
      <li><a href="https://status.github.com/">Status</a></li>
      <li><a href="http://developer.github.com">API</a></li>
      <li><a href="http://training.github.com">Training</a></li>
      <li><a href="http://shop.github.com">Shop</a></li>
      <li><a href="/blog">Blog</a></li>
      <li><a href="/about">About</a></li>

    </ul>

    <a href="/">
      <span class="mega-octicon octicon-mark-github" title="GitHub"></span>
    </a>

    <ul class="site-footer-links">
      <li>&copy; 2014 <span title="0.02619s from github-fe136-cp1-prd.iad.github.net">GitHub</span>, Inc.</li>
        <li><a href="/site/terms">Terms</a></li>
        <li><a href="/site/privacy">Privacy</a></li>
        <li><a href="/security">Security</a></li>
        <li><a href="/contact">Contact</a></li>
    </ul>
  </div><!-- /.site-footer -->
</div><!-- /.container -->


    <div class="fullscreen-overlay js-fullscreen-overlay" id="fullscreen_overlay">
  <div class="fullscreen-container js-fullscreen-container">
    <div class="textarea-wrap">
      <textarea name="fullscreen-contents" id="fullscreen-contents" class="js-fullscreen-contents" placeholder="" data-suggester="fullscreen_suggester"></textarea>
    </div>
  </div>
  <div class="fullscreen-sidebar">
    <a href="#" class="exit-fullscreen js-exit-fullscreen tooltipped tooltipped-w" aria-label="Exit Zen Mode">
      <span class="mega-octicon octicon-screen-normal"></span>
    </a>
    <a href="#" class="theme-switcher js-theme-switcher tooltipped tooltipped-w"
      aria-label="Switch themes">
      <span class="octicon octicon-color-mode"></span>
    </a>
  </div>
</div>



    <div id="ajax-error-message" class="flash flash-error">
      <span class="octicon octicon-alert"></span>
      <a href="#" class="octicon octicon-remove-close close js-ajax-error-dismiss"></a>
      Something went wrong with that request. Please try again.
    </div>

  </body>
</html>

