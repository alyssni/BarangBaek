package com.barangbaek.dao;

import com.barangbaek.bean.user;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class userdao {

    private Connection conn;

    public userdao() {
    }

    public userdao(Connection conn) {
        this.conn = conn;
    }

    // register user
    public boolean registerUser(user u)
            throws SQLException {

        String sql =
                "INSERT INTO USERS ("
                + "Username, "
                + "FullName, "
                + "Birthday, "
                + "Gender, "
                + "UserPhoto, "
                + "Email, "
                + "Address1, "
                + "Address2, "
                + "City, "
                + "State, "
                + "Postcode, "
                + "Phone, "
                + "University, "
                + "Password"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getFullName());

            ps.setDate(
                    3,
                    Date.valueOf(u.getBirthday())
            );

            ps.setString(4, u.getGender());
            ps.setString(5, u.getUserPhoto());
            ps.setString(6, u.getEmail());
            ps.setString(7, u.getAddress1());
            ps.setString(8, u.getAddress2());
            ps.setString(9, u.getCity());
            ps.setString(10, u.getState());
            ps.setString(11, u.getPostcode());
            ps.setString(12, u.getPhone());
            ps.setString(13, u.getUniversity());
            ps.setString(14, u.getPassword());

            return ps.executeUpdate() > 0;
        }
    }

    // login using username and password
    public user login(
            String username,
            String password
    ) throws SQLException {

        String sql =
                "SELECT * FROM USERS "
                + "WHERE LOWER(Username) = LOWER(?) "
                + "AND Password = ?";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (
                ResultSet rs = ps.executeQuery()
            ) {

                if (rs.next()) {

                    /*
                     * Do not place the password inside the
                     * returned user object after login.
                     */
                    return mapUser(rs, false);
                }
            }
        }

        return null;
    }

    // get user by id
    public user getUserByID(int userID)
            throws SQLException {

        String sql =
                "SELECT * FROM USERS WHERE UserID = ?";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setInt(1, userID);

            try (
                ResultSet rs = ps.executeQuery()
            ) {

                if (rs.next()) {
                    return mapUser(rs, true);
                }
            }
        }

        return null;
    }

    // get public seller profile
    public user getPublicSellerByID(int userID)
            throws SQLException {

        String sql =
                "SELECT UserID, Username, FullName, "
                + "UserPhoto, University "
                + "FROM USERS WHERE UserID = ?";

        try (
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, userID);

            try (
                ResultSet rs = ps.executeQuery()
            ) {
                if (rs.next()) {
                    user seller = new user();
                    seller.setUserID(rs.getInt("UserID"));
                    seller.setUsername(rs.getString("Username"));
                    seller.setFullName(rs.getString("FullName"));
                    seller.setUserPhoto(rs.getString("UserPhoto"));
                    seller.setUniversity(rs.getString("University"));
                    return seller;
                }
            }
        }

        return null;
    }

    // user profile update
    public boolean updateProfile(user u)
            throws SQLException {

        String sql =
                "UPDATE USERS SET "
                + "Username = ?, "
                + "FullName = ?, "
                + "Address1 = ?, "
                + "Address2 = ?, "
                + "City = ?, "
                + "State = ?, "
                + "Postcode = ?, "
                + "Phone = ?, "
                + "University = ? "
                + "WHERE UserID = ?";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getFullName());
            ps.setString(3, u.getAddress1());
            ps.setString(4, u.getAddress2());
            ps.setString(5, u.getCity());
            ps.setString(6, u.getState());
            ps.setString(7, u.getPostcode());
            ps.setString(8, u.getPhone());
            ps.setString(9, u.getUniversity());
            ps.setInt(10, u.getUserID());

            return ps.executeUpdate() > 0;
        }
    }

    // user photo update
    public boolean updateUserPhoto(
            int userID,
            String photoName
    ) throws SQLException {

        String sql =
                "UPDATE USERS "
                + "SET UserPhoto = ? "
                + "WHERE UserID = ?";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, photoName);
            ps.setInt(2, userID);

            return ps.executeUpdate() > 0;
        }
    }

    // verify user for password reset
    public user findUserForPasswordReset(
            String email,
            String phone,
            String birthday
    ) throws SQLException {

        String sql =
                "SELECT * FROM USERS "
                + "WHERE LOWER(Email) = LOWER(?) "
                + "AND Phone = ? "
                + "AND Birthday = ?";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, email);
            ps.setString(2, phone);

            ps.setDate(
                    3,
                    Date.valueOf(birthday)
            );

            try (
                ResultSet rs = ps.executeQuery()
            ) {

                if (rs.next()) {
                    return mapUser(rs, false);
                }
            }
        }

        return null;
    }

    // update password
    public boolean updatePassword(
            int userID,
            String newPassword
    ) throws SQLException {

        String sql =
                "UPDATE USERS "
                + "SET Password = ? "
                + "WHERE UserID = ?";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, newPassword);
            ps.setInt(2, userID);

            return ps.executeUpdate() > 0;
        }
    }

    // save remember me token
    public void saveRememberToken(
            int userID,
            String token,
            Timestamp expiryDate
    ) throws SQLException {

        String sql =
                "INSERT INTO REMEMBER_TOKENS "
                + "(UserID, Token, ExpiryDate) "
                + "VALUES (?, ?, ?)";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setInt(1, userID);
            ps.setString(2, token);
            ps.setTimestamp(3, expiryDate);

            ps.executeUpdate();
        }
    }

    // get user remember me token
    public user getUserByRememberToken(String token)
            throws SQLException {

        String sql =
                "SELECT u.* "
                + "FROM USERS u "
                + "INNER JOIN REMEMBER_TOKENS t "
                + "ON u.UserID = t.UserID "
                + "WHERE t.Token = ? "
                + "AND t.ExpiryDate > CURRENT_TIMESTAMP";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, token);

            try (
                ResultSet rs = ps.executeQuery()
            ) {

                if (rs.next()) {
                    return mapUser(rs, false);
                }
            }
        }

        return null;
    }

    // delete a remember me token
    public void deleteRememberToken(String token)
            throws SQLException {

        String sql =
                "DELETE FROM REMEMBER_TOKENS "
                + "WHERE Token = ?";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, token);
            ps.executeUpdate();
        }
    }

    // delete all tokens of a user
    public void deleteRememberTokensByUser(int userID)
            throws SQLException {

        String sql =
                "DELETE FROM REMEMBER_TOKENS "
                + "WHERE UserID = ?";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setInt(1, userID);
            ps.executeUpdate();
        }
    }

    // delete expired tokens (= 1 month)
    public void deleteExpiredRememberTokens()
            throws SQLException {

        String sql =
                "DELETE FROM REMEMBER_TOKENS "
                + "WHERE ExpiryDate <= CURRENT_TIMESTAMP";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.executeUpdate();
        }
    }

    // check for unique username
    public boolean usernameExists(String username)
            throws SQLException {

        String sql =
                "SELECT UserID FROM USERS "
                + "WHERE LOWER(Username) = LOWER(?)";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);

            try (
                ResultSet rs = ps.executeQuery()
            ) {
                return rs.next();
            }
        }
    }

    // check for unique email
    public boolean emailExists(String email)
            throws SQLException {

        String sql =
                "SELECT UserID FROM USERS "
                + "WHERE LOWER(Email) = LOWER(?)";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, email);

            try (
                ResultSet rs = ps.executeQuery()
            ) {
                return rs.next();
            }
        }
    }

    // check username when updating profile
    public boolean usernameExistsForOtherUser(
            String username,
            int currentUserID
    ) throws SQLException {

        String sql =
                "SELECT UserID FROM USERS "
                + "WHERE LOWER(Username) = LOWER(?) "
                + "AND UserID <> ?";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);
            ps.setInt(2, currentUserID);

            try (
                ResultSet rs = ps.executeQuery()
            ) {
                return rs.next();
            }
        }
    }

    // check email when updating email
    public boolean emailExistsForOtherUser(
            String email,
            int currentUserID
    ) throws SQLException {

        String sql =
                "SELECT UserID FROM USERS "
                + "WHERE LOWER(Email) = LOWER(?) "
                + "AND UserID <> ?";

        try (
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, email);
            ps.setInt(2, currentUserID);

            try (
                ResultSet rs = ps.executeQuery()
            ) {
                return rs.next();
            }
        }
    }

    // map database result for user object
    private user mapUser(
            ResultSet rs,
            boolean includePassword
    ) throws SQLException {

        user u = new user();

        u.setUserID(
                rs.getInt("UserID")
        );

        u.setUsername(
                rs.getString("Username")
        );

        u.setFullName(
                rs.getString("FullName")
        );

        Date birthdayDate =
                rs.getDate("Birthday");

        if (birthdayDate != null) {

            u.setBirthday(
                    birthdayDate.toString()
            );

        } else {

            u.setBirthday(null);
        }

        u.setGender(
                rs.getString("Gender")
        );

        u.setUserPhoto(
                rs.getString("UserPhoto")
        );

        u.setEmail(
                rs.getString("Email")
        );

        u.setAddress1(
                rs.getString("Address1")
        );

        u.setAddress2(
                rs.getString("Address2")
        );

        u.setCity(
                rs.getString("City")
        );

        u.setState(
                rs.getString("State")
        );

        u.setPostcode(
                rs.getString("Postcode")
        );

        u.setPhone(
                rs.getString("Phone")
        );

        u.setUniversity(
                rs.getString("University")
        );

        if (includePassword) {

            u.setPassword(
                    rs.getString("Password")
            );
        }

        return u;
    }
}