/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera.ext.media;

import org.apache.abdera.util.AbstractExtensionFactory;

public final class MediaExtensionFactory extends AbstractExtensionFactory implements MediaConstants {

    @SuppressWarnings("deprecation")
    public MediaExtensionFactory() {
        super(MediaConstants.MEDIA_NS);
        addImpl(ADULT, MediaAdult.class);
        addImpl(CATEGORY, MediaCategory.class);
        addImpl(CONTENT, MediaContent.class);
        addImpl(COPYRIGHT, MediaCopyright.class);
        addImpl(CREDIT, MediaCredit.class);
        addImpl(DESCRIPTION, MediaDescription.class);
        addImpl(GROUP, MediaGroup.class);
        addImpl(HASH, MediaHash.class);
        addImpl(KEYWORDS, MediaKeywords.class);
        addImpl(PLAYER, MediaPlayer.class);
        addImpl(RATING, MediaRating.class);
        addImpl(RESTRICTION, MediaRestriction.class);
        addImpl(TEXT, MediaText.class);
        addImpl(THUMBNAIL, MediaThumbnail.class);
        addImpl(TITLE, MediaTitle.class);
    }

}
