package com.ariesr91.ifpeditor;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.*;
import android.view.*;
import android.widget.*;
import android.provider.MediaStore;
import android.content.ContentValues;
import android.os.Environment;
import android.util.Base64;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends Activity {
  private WebView web; private ValueCallback<Uri[]> uploadCallback;
  private static final int FILE_CHOOSER = 2041;
  private static final int SAVE_FILE_CHOOSER = 2042;
  private byte[] pendingExportData;
  private String pendingExportMime = "application/octet-stream";
  private void applyOrientationUi() { if (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) { getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION); } else { getWindow().getDecorView().setSystemUiVisibility(0); } }
  @Override public void onConfigurationChanged(android.content.res.Configuration newConfig) { super.onConfigurationChanged(newConfig); applyOrientationUi(); }
  @Override public void onCreate(Bundle b) { super.onCreate(b); getWindow().setStatusBarColor(0xFF0B0D12); getWindow().setNavigationBarColor(0xFF0B0D12); applyOrientationUi();
    web = new WebView(this); web.setBackgroundColor(0xFF0B0D12); web.setOverScrollMode(View.OVER_SCROLL_NEVER);
    WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setAllowFileAccess(true); s.setAllowContentAccess(true); s.setSupportZoom(true); s.setBuiltInZoomControls(false); s.setDisplayZoomControls(false); s.setJavaScriptCanOpenWindowsAutomatically(true); s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
    web.setWebChromeClient(new WebChromeClient(){
      @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> cb, FileChooserParams params) { if(uploadCallback!=null) uploadCallback.onReceiveValue(null); uploadCallback=cb; Intent i=params.createIntent(); try { startActivityForResult(i,FILE_CHOOSER); } catch(Exception e){uploadCallback=null; Toast.makeText(MainActivity.this,"Tidak dapat membuka pemilih file",Toast.LENGTH_LONG).show(); return false;} return true; }
    });
    web.addJavascriptInterface(new DownloadBridge(), "AndroidDownload");
    web.setWebViewClient(new WebViewClient(){ @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r){ String u=r.getUrl().toString(); if(u.startsWith("https://gtastuff.com/")||u.startsWith("https://www.gtastuff.com/")) return false; if(u.startsWith("https://")||u.startsWith("http://")){ try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(u)));}catch(Exception ignored){} return true;} return false; }
      @Override public void onPageFinished(WebView v,String url){ super.onPageFinished(v,url); installBlobDownloadHandler(v); v.evaluateJavascript("(function(){var s=document.getElementById('ifp-android-style');if(!s){s=document.createElement('style');s.id='ifp-android-style';}s.textContent=`html{font-size:16px;-webkit-text-size-adjust:100%;}body{padding-bottom:env(safe-area-inset-bottom)!important;}button,.btn,input,select,textarea{min-height:42px;touch-action:manipulation;}button,.btn{padding:10px 13px!important;}input,select,textarea{font-size:16px!important;max-width:100%;} .app,.container,.main-container{max-width:100%!important;}canvas{max-width:100%;} @media(max-width:700px){body{overflow-x:hidden!important}.toolbar,.topbar,.header{flex-wrap:wrap!important;gap:8px!important}.layout,.editor-layout,.main-layout{display:flex!important;flex-direction:column!important}.sidebar,.panel{width:100%!important;max-width:100%!important;box-sizing:border-box}.modal,.dialog{max-width:calc(100vw - 24px)!important}.btn-icon{min-width:42px!important;min-height:42px!important}#animation-list{max-height:30vh!important;overflow:auto!important}} @media(orientation:landscape){html,body{width:100%!important;height:100%!important;margin:0!important;overflow:hidden!important}#app{display:flex!important;flex-direction:row!important;width:100vw!important;height:100vh!important;height:100dvh!important;min-height:0!important;max-height:100dvh!important;overflow:hidden!important}#main-area{order:1!important;flex:1 1 69%!important;width:69%!important;max-width:69%!important;height:100%!important;min-width:0!important;min-height:0!important;overflow:hidden!important;display:flex!important;flex-direction:column!important}#sidebar{order:2!important;flex:0 0 31%!important;width:31%!important;min-width:0!important;max-width:31%!important;height:100%!important;max-height:100%!important;box-sizing:border-box!important;overflow-y:auto!important;overflow-x:hidden!important;-webkit-overflow-scrolling:touch!important;overscroll-behavior-y:contain!important;touch-action:pan-y!important;border-right:0!important;border-left:1px solid var(--border)!important;border-bottom:0!important;padding:10px!important}#viewport{position:relative!important;flex:0 0 52dvh!important;width:100%!important;height:52dvh!important;min-height:180px!important;max-height:60dvh!important;overflow:hidden!important}#canvas{width:100%!important;height:100%!important}#main-area>*{flex-shrink:0}#main-area .timeline,#main-area #timeline-container,#main-area #timeline{min-height:0!important;max-height:48dvh!important;overflow:auto!important}} @media(orientation:landscape) and (max-height:500px){header{padding-top:4px!important;padding-bottom:4px!important}.panel{max-height:75vh;overflow:auto}}`;s.textContent+=' @media(orientation:landscape){html,body{height:100%!important;overflow:hidden!important}#app{display:grid!important;grid-template-columns:minmax(0,69fr) minmax(240px,31fr)!important;grid-template-rows:100dvh!important;width:100vw!important;height:100dvh!important;overflow:hidden!important}#main-area{grid-column:1!important;grid-row:1!important;display:flex!important;flex-direction:column!important;width:100%!important;max-width:none!important;height:100%!important;min-width:0!important;min-height:0!important;overflow:hidden!important}#sidebar{grid-column:2!important;grid-row:1!important;width:100%!important;min-width:0!important;max-width:none!important;height:100%!important;max-height:100%!important;box-sizing:border-box!important;overflow-y:auto!important;overflow-x:hidden!important;overscroll-behavior:contain!important;-webkit-overflow-scrolling:touch!important;border-left:1px solid var(--border)!important;border-right:0!important}#viewport{flex:0 0 52dvh!important;height:52dvh!important;min-height:180px!important;max-height:60dvh!important;overflow:hidden!important}} @media(orientation:portrait){html,body{height:100%!important;overflow:hidden!important}#app{display:flex!important;flex-direction:column!important;width:100vw!important;height:100dvh!important;overflow:hidden!important}#main-area{order:0!important;flex:0 0 auto!important;width:100%!important;height:auto!important;min-height:0!important;max-height:none!important;overflow:hidden!important}#viewport{flex:0 0 35dvh!important;height:35dvh!important;min-height:170px!important;max-height:42dvh!important}#sidebar{order:1!important;flex:1 1 0!important;width:100%!important;min-width:0!important;max-width:100%!important;min-height:0!important;height:auto!important;max-height:none!important;overflow-y:auto!important;overflow-x:hidden!important;overscroll-behavior:contain!important;-webkit-overflow-scrolling:touch!important}} @media(orientation:landscape){#app{grid-template-columns:minmax(0,1fr) minmax(0,1fr)!important;grid-template-rows:100dvh!important}@media(orientation:landscape){#app{display:flex!important;flex-direction:row!important;flex-wrap:nowrap!important;width:100vw!important;height:100dvh!important;overflow:hidden!important}#main-area{order:1!important;flex:0 0 50%!important;width:50%!important;max-width:50%!important;min-width:0!important;height:100dvh!important}#sidebar{order:2!important;flex:0 0 50%!important;width:50%!important;max-width:50%!important;min-width:0!important;height:100dvh!important;max-height:100dvh!important;box-sizing:border-box!important;overflow-y:auto!important;overflow-x:hidden!important}}#main-area{grid-column:1!important;grid-row:1!important;width:100%!important;max-width:none!important;height:100dvh!important;min-height:0!important;overflow:hidden!important;display:flex!important;flex-direction:column!important}#sidebar{grid-column:2!important;grid-row:1!important;width:100%!important;max-width:none!important;min-width:0!important;height:100dvh!important;max-height:100dvh!important;overflow-y:auto!important;overflow-x:hidden!important}#viewport{flex:1 1 0!important;width:100%!important;height:auto!important;min-height:0!important;max-height:none!important;overflow:hidden!important}#canvas{width:100%!important;height:100%!important;max-height:100%!important}#main-area>*{flex-shrink:0!important}#main-area>#viewport{flex:1 1 0!important;min-height:0!important}#main-area .timeline,#main-area #timeline-container,#main-area #timeline{flex:0 0 auto!important;max-height:22dvh!important;min-height:0!important;overflow:auto!important}}';document.head.appendChild(s);document.title='SR IFP Editor';var brandStyle=document.getElementById('ifp-sr-branding');if(!brandStyle){brandStyle=document.createElement('style');brandStyle.id='ifp-sr-branding';document.head.appendChild(brandStyle);}brandStyle.textContent='#sidebar img[alt*=\"gtastuff\" i],#sidebar img[src*=\"gtastuff\" i],#sidebar [class*=\"logo\" i],#sidebar [id*=\"logo\" i],#sidebar .brand,#sidebar .site-logo{display:none!important}';function setSrTitle(){document.title='SR IFP Editor';document.querySelectorAll('h1,h2,h3,.title,.app-title').forEach(function(el){if(/GTA Stuff|IFP Animation Editor/i.test(el.textContent||''))el.textContent='SR IFP Editor';});}setSrTitle();var dz=document.getElementById('drop-zone');if(dz){var label=dz.querySelector('p');if(label)label.textContent='Open IFP/DFF/TXD';}var extra=document.getElementById('ifp-open-button-style');if(!extra){extra=document.createElement('style');extra.id='ifp-open-button-style';extra.textContent='#drop-zone{box-sizing:border-box!important;width:100%!important;min-height:44px!important;height:44px!important;padding:0 12px!important;margin:8px 0 10px!important;display:flex!important;align-items:center!important;justify-content:center!important;border:1px solid var(--border)!important;border-radius:8px!important;background:var(--surface-2,var(--panel))!important;cursor:pointer!important;overflow:hidden!important}#drop-zone .drop-zone-content{display:flex!important;align-items:center!important;justify-content:center!important;width:100%!important;height:100%!important;gap:0!important;padding:0!important}#drop-zone svg,#drop-zone .hint{display:none!important}#drop-zone p{margin:0!important;padding:0!important;font-size:14px!important;font-weight:600!important;line-height:1.2!important}#drop-zone input[type=file]{position:absolute!important;inset:0!important;width:100%!important;height:100%!important;opacity:0!important;cursor:pointer!important}#drop-zone:hover{filter:brightness(1.12)}';document.head.appendChild(extra);}var portrait=document.getElementById('ifp-portrait-icon-style');if(!portrait){portrait=document.createElement('style');portrait.id='ifp-portrait-icon-style';portrait.textContent='@media (orientation:portrait){.anim-actions #btn-rename,.anim-actions #btn-duplicate,.anim-actions #btn-replace,.anim-actions #btn-delete{font-size:0!important;gap:0!important;min-width:42px!important;width:42px!important;padding:10px!important;justify-content:center!important;white-space:nowrap!important}.anim-actions #btn-rename svg,.anim-actions #btn-duplicate svg,.anim-actions #btn-replace svg,.anim-actions #btn-delete svg{flex-shrink:0!important;width:18px!important;height:18px!important}}';document.head.appendChild(portrait);}})();",null); v.evaluateJavascript("(function(){var s=document.getElementById(\'ifp-action-icons\');if(!s){s=document.createElement(\'style\');s.id=\'ifp-action-icons\';document.head.appendChild(s);}s.textContent=\'#btn-rename,#btn-duplicate,#btn-replace,#btn-delete{font-size:0!important;gap:0!important;width:42px!important;min-width:42px!important;max-width:42px!important;padding:10px!important;justify-content:center!important;white-space:nowrap!important;overflow:hidden!important}#btn-rename svg,#btn-duplicate svg,#btn-replace svg,#btn-delete svg{display:inline-block!important;flex-shrink:0!important;width:18px!important;height:18px!important}#btn-rename *,#btn-duplicate *,#btn-replace *,#btn-delete *{font-size:0!important}\';})()",null);  }
    });
    web.setDownloadListener((url,ua,cd,mime,len)->{try{android.app.DownloadManager.Request req=new android.app.DownloadManager.Request(Uri.parse(url)); req.setMimeType(mime); req.addRequestHeader("User-Agent",ua); req.setTitle(cd!=null?URLUtil.guessFileName(url,cd,mime):URLUtil.guessFileName(url,null,mime)); req.setDescription("Mengunduh file hasil IFP Editor"); req.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); req.setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(url,cd,mime)); ((android.app.DownloadManager)getSystemService(DOWNLOAD_SERVICE)).enqueue(req); Toast.makeText(this,"Unduhan dimulai",Toast.LENGTH_SHORT).show();}catch(Exception e){Toast.makeText(this,"Unduhan gagal: "+e.getMessage(),Toast.LENGTH_LONG).show();}});
    setContentView(web); web.loadUrl("https://gtastuff.com/tools/ifp-editor/");
  }
  @Override public void onBackPressed(){if(web!=null&&web.canGoBack())web.goBack();else super.onBackPressed();}

  private void installBlobDownloadHandler(WebView view) {
    String js = "(function(){"
      + "if(window.__srBlobDownloadInstalled)return;window.__srBlobDownloadInstalled=true;"
      + "function exportBlob(a,href){if(!href||href.indexOf('blob:')!==0)return false;"
      + "if(a&&a.__srExporting)return true;if(a)a.__srExporting=true;"
      + "var name=(a&&(a.getAttribute('download')||a.getAttribute('data-filename')))||'export.ifp';"
      + "fetch(href).then(function(r){if(!r.ok)throw new Error('read');return r.blob()})"
      + ".then(function(blob){var reader=new FileReader();reader.onloadend=function(){"
      + "var result=String(reader.result||'');var comma=result.indexOf(',');"
      + "if(comma<0){alert('Ekspor gagal: data file tidak terbaca.');return;}"
      + "if(!window.AndroidDownload||!window.AndroidDownload.saveFile){alert('Fitur simpan Android tidak tersedia.');return;}"
      + "window.AndroidDownload.saveFile(name,blob.type||'application/octet-stream',result.substring(comma+1));};"
      + "reader.onerror=function(){alert('Ekspor gagal saat membaca file.');};reader.readAsDataURL(blob);})"
      + ".catch(function(){if(a)a.__srExporting=false;alert('Ekspor gagal saat menyiapkan file.');});return true;}"
      + "document.addEventListener('click',function(e){var a=e.target&&e.target.closest?e.target.closest('a'):null;"
      + "if(!a)return;var href=a.href||a.getAttribute('href')||'';if(href.indexOf('blob:')!==0)return;"
      + "e.preventDefault();e.stopImmediatePropagation();exportBlob(a,href);},true);"
      + "var originalClick=HTMLAnchorElement.prototype.click;"
      + "HTMLAnchorElement.prototype.click=function(){var href=this.href||this.getAttribute('href')||'';"
      + "if(href.indexOf('blob:')===0){exportBlob(this,href);return;}return originalClick.apply(this,arguments);};"
      + "})();";
    view.evaluateJavascript(js, null);
  }

  private class DownloadBridge {
    @JavascriptInterface public void saveFile(String fileName, String mimeType, String base64) {
      try {
        String safeName = fileName == null ? "export.ifp" : fileName.replaceAll("[/\\\\:*?\"<>|]", "_");
        if (safeName.trim().isEmpty()) safeName = "export.ifp";
        pendingExportData = Base64.decode(base64, Base64.DEFAULT);
        pendingExportMime = (mimeType == null || mimeType.isEmpty()) ? "application/octet-stream" : mimeType;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(pendingExportMime);
        intent.putExtra(Intent.EXTRA_TITLE, safeName);
        startActivityForResult(intent, SAVE_FILE_CHOOSER);
      } catch (Exception ex) {
        pendingExportData = null;
        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Tidak dapat membuka pemilih lokasi penyimpanan: " + ex.getMessage(), Toast.LENGTH_LONG).show());
      }
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == FILE_CHOOSER) {
      if (uploadCallback != null) {
        Uri[] results = (resultCode == RESULT_OK && data != null) ? WebChromeClient.FileChooserParams.parseResult(resultCode, data) : null;
        uploadCallback.onReceiveValue(results);
        uploadCallback = null;
      }
      return;
    }
    if (requestCode == SAVE_FILE_CHOOSER) {
      byte[] bytes = pendingExportData;
      pendingExportData = null;
      if (resultCode != RESULT_OK || data == null || data.getData() == null) {
        Toast.makeText(this, "Penyimpanan dibatalkan.", Toast.LENGTH_SHORT).show();
        return;
      }
      if (bytes == null) {
        Toast.makeText(this, "Ekspor gagal: data file kosong.", Toast.LENGTH_LONG).show();
        return;
      }
      try (OutputStream out = getContentResolver().openOutputStream(data.getData(), "w")) {
        if (out == null) throw new java.io.IOException("Lokasi tidak dapat ditulis");
        out.write(bytes);
        out.flush();
        Toast.makeText(this, "File IFP berhasil disimpan.", Toast.LENGTH_LONG).show();
      } catch (Exception ex) {
        Toast.makeText(this, "Gagal menyimpan file: " + ex.getMessage(), Toast.LENGTH_LONG).show();
      }
    }
  }

}
