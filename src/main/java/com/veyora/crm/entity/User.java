package com.veyora.crm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.veyora.crm.constant.RoleType;
import com.veyora.crm.constant.RoleTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    public static final String ACTIVATED_YES = "Y";
    public static final String ACTIVATED_NO = "N";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "create_ts", updatable = false)
    private Instant createTs;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "role", nullable = false, columnDefinition = "char(1)")
    private String role = "U";

    @Convert(converter = RoleTypeConverter.class)
    @Column(name = "roletype")
    private RoleType roleType = RoleType.USER;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "activated", columnDefinition = "char(1)")
    private String activated = ACTIVATED_YES;

    @Column(nullable = false, length = 151)
    private String name;

    @Column(name = "a_street", length = 151)
    private String street;

    @Column(name = "a_city", length = 51)
    private String city;

    @Column(name = "a_state", length = 51)
    private String state;

    @Column(name = "a_pincode", length = 11)
    private String pincode;

    @Column(name = "a_country", length = 51)
    private String country;

    @Column(length = 101)
    private String phone;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "e_verified", columnDefinition = "char(1)")
    private String emailVerified = "N";

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "e_activated", columnDefinition = "char(1)")
    private String emailActivated = "N";

    @Column(length = 101)
    private String email;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "m_verified", columnDefinition = "char(1)")
    private String mobileVerified = "N";

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "m_activated", columnDefinition = "char(1)")
    private String mobileActivated = "N";

    @Column(length = 16)
    private String mobile;

    @JsonIgnore
    @Column(nullable = false, length = 101)
    private String password;

    @JsonIgnore
    @Column(name = "txn_password", length = 101)
    private String txnPassword;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "pageownerid")
    private Long pageOwnerId = 0L;

    @Column(name = "adminids", length = 256)
    private String adminIds;

    @Column(name = "useracl")
    private Long userAcl = 0L;

    @Column(length = 36, unique = true)
    private String uuid;

    @Column(name = "profile_pic_url", length = 128)
    private String profilePicUrl;

    @Column(name = "login_partner_id")
    private Integer loginPartnerId = -1;

    @Column(name = "managed_by", length = 101)
    private String managedBy;

    @Column(length = 100)
    private String grade;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "base_currency", columnDefinition = "char(3)")
    private String baseCurrency;

    @PrePersist
    void prePersist() {
        if (createTs == null) {
            createTs = Instant.now();
        }
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (role == null && roleType != null) {
            role = roleType.getRoleChar();
        }
    }

    @JsonIgnore
    public boolean isActivatedUser() {
        return ACTIVATED_YES.equalsIgnoreCase(activated);
    }
}
