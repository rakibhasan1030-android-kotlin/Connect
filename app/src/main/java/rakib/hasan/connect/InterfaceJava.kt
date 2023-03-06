package rakib.hasan.connect

import android.webkit.JavascriptInterface

class InterfaceJava(var callActivity: CallActivity) {

    @JavascriptInterface
    fun onPeerConnected() = callActivity.onPeerConnected()
}
