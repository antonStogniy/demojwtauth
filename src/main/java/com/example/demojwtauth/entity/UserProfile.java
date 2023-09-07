package com.example.demojwtauth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_profile")
@AllArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    @Column(name = "phone_number")
    @Size(max = 15)
    private String phoneNumber;


    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @Temporal(TemporalType.DATE)
    @Column(name = "dob")
    private Date dateOfBirth;
    @Size(max = 100)
    private String address1;
    @Size(max = 100)
    private String address2;
    @Size(max = 100)
    private String street;
    @Size(max = 100)
    private String state;
    @Size(max = 100)
    private String country;
    @Column(name = "zip_code")
    @Size(max = 32)
    private String zipCode;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    private User user;
    @OneToMany(mappedBy = "userProfile")
    private List<Post> posts = new ArrayList<>();
}
