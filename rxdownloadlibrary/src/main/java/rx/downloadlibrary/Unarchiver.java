package rx.downloadlibrary;

import android.util.Log;

import org.apache.commons.io.FilenameUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

public class Unarchiver {
    private static final String LOG_TAG = "Unarchiver";

    private File mTarGz, mDestinationDir;
    private boolean mShouldDeleteSource;

    public Unarchiver(File tarGz, boolean shouldDeleteSource) {
        mTarGz = tarGz;
        mDestinationDir = new File(tarGz.getParent() + File.separator + FilenameUtils.removeExtension(tarGz.getName()));
        mDestinationDir.mkdirs();
        mShouldDeleteSource = shouldDeleteSource;
    }

    public File unarchive() {
        try {
            FileInputStream inputStream = new FileInputStream(mTarGz);
            GZIPInputStream gzipInStream = new GZIPInputStream(inputStream);
            TarInputStream tarInStream = new TarInputStream(gzipInStream);

            TarEntry tarEntry = null;
            while ((tarEntry = tarInStream.getNextEntry()) != null) {
//                Log.d("Unzip", "Unzipping : " + tarEntry.getDefaultName() + " -- to : " + mDestinationDir.getDefaultName());

                File file = new File(mDestinationDir.getAbsolutePath() + File.separator + tarEntry.getName());
                if (tarEntry.isDirectory()) {
                    file.mkdirs();
                    continue;
                }

                File parent = file.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }

                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();

                OutputStream fOutStream = new FileOutputStream(file);
                tarInStream.copyEntryContents(fOutStream);
                fOutStream.close();
            }

            // Delete the original archive
            if (mTarGz.exists() && mShouldDeleteSource) {
                mTarGz.delete();
            }

            // Close the streams
            tarInStream.close();
            gzipInStream.close();
            inputStream.close();

        } catch (final Exception e) {
            Log.e(LOG_TAG, "Failed to UnGZip : " + e);
        }

        return mDestinationDir;
    }
}