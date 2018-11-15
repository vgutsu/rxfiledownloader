//package rx.downloadlibrary;
//
//import android.content.Context;
//import android.webkit.MimeTypeMap;
//
//import java.io.File;
//
//
//public class Utility {
//
//    public static String getMimeType(String filename) {
//        int dotIndex = filename.lastIndexOf(".");
//
//        if (dotIndex >= 0) {
//            String extension = filename.substring(dotIndex + 1);
//
//            if (extension.length() > 0) {
//                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//
//                if (mimeType != null)
//                    return mimeType;
//            }
//        }
//
//        return "*/*";
//    }
//
//    // Static Methods
//    public static Boolean isFileExist(Context context, String fileName) {
//        File packageDir = getPackageDir(context, fileName);
//        if (packageDir.exists()) {
//            return true;
//        }
//        return false;
//    }
//
//    public static String getPackagePath(Context context, String uid) {
//        return context.getFilesDir() + File.separator + uid;
//    }
//
//    public static File getPackageDir(Context context, String popMapUid) {
//        File packageDir = new File(getPackagePath(context, popMapUid));
//        return packageDir;
//    }
//
//    public static File getPackageTarGz(Context context, String popMapUid) {
//        File packageTgzFile = new File(getPackagePath(context, popMapUid) + ".tar.gz");
//        return packageTgzFile;
//    }
//
//}
