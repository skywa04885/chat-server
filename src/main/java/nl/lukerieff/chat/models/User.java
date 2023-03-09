package nl.lukerieff.models;

import dev.morphia.annotations.*;
import dev.morphia.utils.IndexType;
import org.bson.types.ObjectId;

import java.util.List;

@Entity("user")
@Indexes({
        @Index(
                fields = @Field(
                        value = "username",
                        type = IndexType.HASHED
                )
        ),
        @Index(
                fields = @Field(
                        value = "email",
                        type = IndexType.HASHED
                )
        )
})
public class User {
    @Entity("user_meta")
    public static class Meta {
        private Long creationDate;
        private Long lastOnelineDate;

        public Meta(final Long creationDate, final Long lastOnelineDate) {
            this.creationDate = creationDate;
            this.lastOnelineDate = lastOnelineDate;
        }
    }

    @Entity("user_profile")
    public static class Profile {
        @Entity("user_profile_avatars")
        public static class Avatars {
            @Entity("user_profile_avatars_avatar")
            public static class Avatar {
                private ObjectId file;
                private Long since;

                public Avatar() {}

                public Avatar(final ObjectId file, final Long since) {
                    this.file = file;
                    this.since = since;
                }

                public ObjectId getFile() {
                    return file;
                }

                public void setFile(ObjectId file) {
                    this.file = file;
                }
            }

            private Avatar current;
            private List<Avatar> history;

            public Avatars() {}

            public Avatars(final Avatar current, final List<Avatar> history) {
                this.current = current;
                this.history = history;
            }

            public Avatar getCurrent() {
                return current;
            }

            public void setCurrent(Avatar current) {
                this.current = current;
            }

            public List<Avatar> getHistory() {
                return history;
            }

            public void setHistory(List<Avatar> history) {
                this.history = history;
            }

        }

        @Entity("user_profile_statuses")
        public static class Statuses {
            @Entity("user_profile_Statuses_status")
            public static class Status {
                private String text;
                private Long since;

                public Status() {}

                public Status(final String text, final Long since) {
                    this.text = text;
                    this.since = since;
                }


                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public Long getSince() {
                    return since;
                }

                public void setSince(long since) {
                    this.since = since;
                }
            }

            private Status current;
            private List<Status> history;

            public Statuses() {}

            public Statuses(final Status current, final List<Status> history) {
                this.current = current;
                this.history = history;
            }

            public Status getCurrent() {
                return current;
            }

            public void setCurrent(Status current) {
                this.current = current;
            }

            public List<Status> getHistory() {
                return history;
            }

            public void setHistory(List<Status> history) {
                this.history = history;
            }
        }

        private Statuses statuses;
        private Avatars avatars;

        public Profile() {}

        public Profile(final Statuses statuses, final Avatars avatars) {
            this.statuses = statuses;
            this.avatars = avatars;
        }

        public void setStatuses(final Statuses statuses) {
            this.statuses = statuses;
        }

        public Statuses getStatuses() {
            return statuses;
        }

        public Avatars getAvatars() {
            return avatars;
        }

        public void setAvatars(final Avatars avatars) {
            this.avatars = avatars;
        }
    }

    @Id
    private ObjectId id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Profile profile;
    private Meta meta;

    public User() {}

    public User(final ObjectId id, final String username, final String email,
                final String fullName, final String phone, final Profile profile,
                final Meta meta) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.profile = profile;
        this.meta = meta;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Meta getMeta() {
        return this.meta;
    }

    public void setMeta(final Meta meta) {
        this.meta = meta;
    }
}
