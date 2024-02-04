package com.tecknobit.monkey;


/**
 * The {@code MonkeyVerificationActions} interface is useful to create a workflow after the verification of the code
 * sent with the email
 *
 * @author N7ghtm4r3 - Tecknobit
 */
public interface MonkeyVerificationActions {

    /**
     * Method invoked when the verification code matches with the code sent by the user
     */
    void onSuccess();

    /**
     * Method invoked when the verification code does not match with the code sent by the user
     * or the verification email is expired
     */
    void onFailure();

}
