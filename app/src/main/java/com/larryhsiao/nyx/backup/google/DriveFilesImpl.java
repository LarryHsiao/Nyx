/**
 * Copyright 2018 Google LLC
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.larryhsiao.nyx.backup.google;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link DriveFiles}.
 * This class creates a folder at users root directory.
 */
public class DriveFilesImpl implements DriveFiles {
    private final Drive mDriveService;
    private final String rootName;

    public DriveFilesImpl(Drive driveService, String rootName) {
        mDriveService = driveService;
        this.rootName = rootName;
    }

    @Override
    public String touch(String name, String mimeType) throws IOException {
        String folderId = rootFolderId();
        List<File> exist = mDriveService.files().list()
            .setQ("name='" + name + "' and '" + folderId + "' in parents and trashed = false")
            .execute()
            .getFiles();
        if (exist.size() > 0) {
            return exist.get(0).getId();
        }

        File metadata = new File()
            .setParents(Collections.singletonList(folderId))
            .setMimeType(mimeType)
            .setName(name);
        File googleFile = mDriveService.files().create(metadata).execute();

        if (googleFile == null) {
            throw new IOException("Null result when requesting file creation.");
        }
        return googleFile.getId();
    }

    private String rootFolderId() throws IOException {
        List<File> files = mDriveService.files().list()
            .setQ("name='" + rootName + "' and mimeType = 'application/vnd.google-apps.folder' and 'root' in parents")
            .execute().getFiles();
        if (files.size() > 0) {
            return files.get(0).getId();
        }

        File fileMetadata = new File();
        fileMetadata.setName(rootName);
        fileMetadata.setParents(Collections.singletonList("root"));
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        File file = mDriveService.files().create(fileMetadata)
            .setFields("id")
            .execute();
        return file.getId();
    }

    @Override
    public void download(String fileId, java.io.File target) throws IOException {
        mDriveService.files().get(fileId).executeMediaAndDownloadTo(new FileOutputStream(target));
    }

    @Override
    public void save(
        String fileId,
        String name,
        InputStream inputStream,
        String mimeType
    ) throws IOException {
        File metadata = new File().setName(name);
        mDriveService.files().update(
            fileId,
            metadata,
            new InputStreamContent(mimeType, inputStream)
        ).execute();
    }

    @Override
    public FileList files(String pageToken) throws IOException {
        Drive.Files.List list = mDriveService.files()
            .list()
            .setQ("trashed = false");
        if (pageToken != null && !pageToken.isEmpty()) {
            list.setPageToken(pageToken);
        }
        return list.setSpaces("drive").execute();
    }

    @Override
    public FileList files() throws IOException {
        return files(null);
    }

    @Override
    public List<File> byName(String keyword) throws IOException {
        return mDriveService.files().list()
            .setQ("name='" + keyword + "' and '" + rootFolderId() + "' in parents and trashed = false")
            .execute()
            .getFiles();
    }

    @Override
    public void delete(String fileId) throws IOException {
        mDriveService.files().delete(fileId).execute();
    }

    @Override
    public void clear() throws IOException {
        mDriveService.files().delete(rootFolderId()).execute();
    }
}
