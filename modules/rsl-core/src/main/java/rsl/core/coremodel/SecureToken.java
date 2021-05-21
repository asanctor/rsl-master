package rsl.core.coremodel;


import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import rsl.core.RSL;
import rsl.util.Random;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class SecureToken{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String token;

    @Column(updatable = false)
    private LocalDateTime timeStamp;

    @PrePersist
    public void setCreationDateTime() {
        this.timeStamp = LocalDateTime.now();
    }

    @Column(updatable = false)
    @Basic(optional = false)
    private LocalDateTime expireAt;

    // token belongs to a certain user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private RslUser user;

    @Transient
    private boolean isExpired;


    public SecureToken()
    {
        this(false);
    }

    public SecureToken(Boolean save)
    {
        this.id = Random.randomInt(0, Integer.MAX_VALUE - 1);
        if(save)
        {
            this.save();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends SecureToken> T save()
    {
        RSL.getDB().save(this);
        return (T)this;
    }

    public void refresh()
    {
        RSL.getDB().refresh(this);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public boolean isExpired() {

        return getExpireAt().isBefore(LocalDateTime.now()); // this is generic implementation, you can always make it timezone specific
    }

    public RslUser getUser() {
        return user;
    }

    public void setUser(RslUser user) {
        this.user = user;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecureToken)) return false;
        SecureToken token = (SecureToken) o;
        return id == token.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, "user");
    }
}
