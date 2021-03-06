package net.androidwing.hotxposed;

import android.util.Log;
import dalvik.system.PathClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.io.File;

/**
 * Created  on 2018/3/30.
 */
public class HotXposed {
  public static void hook(Class clazz, XC_LoadPackage.LoadPackageParam lpparam)
      throws Exception {
    File apkFile = getApkFile();

    if (!apkFile.exists()) {
      Log.e("error", "apk file not found");
      XposedBridge.log("未找到apk文件");
      return;
    }
    XposedBridge.log("找到apk文件");

    filterNotify(lpparam);

    PathClassLoader classLoader =
        new PathClassLoader(apkFile.getAbsolutePath(), ClassLoader.getSystemClassLoader());

    XposedHelpers.callMethod(classLoader.loadClass(clazz.getName()).newInstance(), "dispatch",lpparam);

  }

  private static void filterNotify(XC_LoadPackage.LoadPackageParam lpparam)
      throws ClassNotFoundException {
    if("de.robv.android.xposed.installer".equals(lpparam.packageName)){
      XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("de.robv.android.xposed.installer.util.NotificationUtil"),
          "showModulesUpdatedNotification", new XC_MethodHook() {
            @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
              param.setResult(new Object());
            }

            @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              super.afterHookedMethod(param);
            }
          });
    }
  }


  private static File getApkFile() {
    String filePath = String.format("/data/app/%s-%s/base.apk", BuildConfig.APPLICATION_ID, 1);
    File apkFile = new File(filePath);
    if (!apkFile.exists()) {
      filePath = String.format("/data/app/%s-%s/base.apk", BuildConfig.APPLICATION_ID, 2);
      apkFile = new File(filePath);
    }
    return apkFile;
  }
}
