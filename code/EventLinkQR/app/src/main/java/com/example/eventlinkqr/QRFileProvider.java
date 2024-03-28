package com.example.eventlinkqr;

import androidx.core.content.FileProvider;

/** Provide access to files when sharing with other apps */
public class QRFileProvider extends FileProvider {
    public QRFileProvider() {
        super(R.xml.file_paths);
    }
}
