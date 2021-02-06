package com.atanasvasil.mobile.mycardocs.responses.users;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable {

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private Timestamp editedOn;
}
