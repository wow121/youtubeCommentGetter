package com.company;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by xianrui on 2016/11/2.
 */

public class YoutubeCommentDto {


    public String kind;
    public String etag;
    public String nextPageToken;

    public PageInfo pageInfo;

    public List<Comment> items;
    public Replies replies;

    public static class PageInfo {
        public int totalResults;
        public int resultsPerPage;
    }

    public static class Comment {
        public String kind;
        public String etag;
        public String id;

        public Snippet snippet;
        public Replies replies;

        public static class Snippet {
            public String videoId;

            public TopLevelCommentEntity topLevelComment;
            public boolean canReply;
            public int totalReplyCount;
            public boolean isPublic;

            public static class TopLevelCommentEntity {
                public String kind;
                public String etag;
                public String id;

                public SnippetEntity snippet;

                public static class SnippetEntity {
                    public String authorDisplayName;
                    public String authorProfileImageUrl;
                    public String authorChannelUrl;

                    public AuthorChannelIdEntity authorChannelId;
                    public String videoId;
                    public String textDisplay;
                    public boolean canRate;
                    public String viewerRating;
                    public int likeCount;
                    public String publishedAt;
                    public String updatedAt;

                    public static class AuthorChannelIdEntity {
                        public String value;
                    }
                }
            }
        }
    }


    public static class Replies {

        public List<CommentsEntity> comments;

        public static class CommentsEntity {
            public String kind;
            public String etag;
            public String id;

            public SnippetEntity snippet;

            public static class SnippetEntity {
                public String authorDisplayName;
                public String authorProfileImageUrl;
                public String authorChannelUrl;
                public AuthorChannelIdEntity authorChannelId;
                public String videoId;
                public String textDisplay;
                public String parentId;
                public boolean canRate;
                public String viewerRating;
                public int likeCount;
                public String publishedAt;
                public String updatedAt;

                public static class AuthorChannelIdEntity {
                    public String value;
                }
            }
        }
    }

    public static YoutubeCommentDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (YoutubeCommentDto) gson.fromJson(jsonString, new TypeToken<YoutubeCommentDto>() {
        }.getType());
    }
}
