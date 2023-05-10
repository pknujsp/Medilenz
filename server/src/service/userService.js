"use strict";

const { User } = require("../models/index");
const { response } = require("../config/response");
const responseMsg = require("../config/responseMsg");
const { createAccessToken, createRefreshToken } = require("../config/jwt");

// sign-in
const login = async (email, password) => {
    const userInfo = await User.findOne({
        attributes: ["USERID", "PASSWORD"],
        where: {
            EMAIL: email
        }
    });
    if (!userInfo) { // if user not exists
        return response(404, responseMsg.SIGNIN_USER_NOT_FOUND);
    }
    if (password != userInfo.PASSWORD) { // password mismatch
        return response(401, responseMsg.SIGNIN_PASSWORD_MISMATCH);
    }
    const response = responseMsg.SIGNIN_SUCCESS;
    response.accessToken = createAccessToken(userInfo.USERID); // generate access token
    response.refreshToken = createRefreshToken(userInfo.USERID); // generate refresh token

    return response(200, response);
}

// duplicate check
const duplicateCheck = async (parameter) => {
    if (await User.findOne({ // if user exists
        where: parameter
    })) {
        return true;
    }
    return false; // if user not exists
}

// create user
const createUser = async (email, password, nickname) => {
    // duplicate check
    let checkParam = "EMAIL"
    if (await duplicateCheck({ [checkParam]: email })) { // email
        return response(409, responseMsg.SIGNUP_DUPLICATE_PARAMETER(checkParam));
    }
    checkParam = "NICKNAME";
    if (await duplicateCheck({ [checkParam]: nickname })) { // nickname
        return response(409, responseMsg.SIGNUP_DUPLICATE_PARAMETER(checkParam));
    }

    try {
        const user = await User.create({ // insert new User into DB
            EMAIL: email,
            PASSWORD: password,
            NICKNAME: nickname
        });
        const response = responseMsg.SIGNUP_SUCCESS;
        response.accessToken = createAccessToken(user.USERID); // generate access token
        response.refreshToken = createRefreshToken(user.USERID); // generate refresh token

        return response(201, response);
    }
    catch (err) {
        console.log(err);
        return response(500, responseMsg.SIGNUP_INTERNAL_SERVER_ERROR);
    }
}

module.exports = {
    login,
    createUser
}