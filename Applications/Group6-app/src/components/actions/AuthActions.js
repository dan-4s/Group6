import {EMAIL_CHANGED,PASSWORD_CHANGED,LOGIN_USER_SUCCESS,LOGIN_USER_FAIL,LOGIN_USER,EMAIL_SIGNUP_CHANGED,PASSWORD_SIGNUP_CHANGED,PASSWORDCONF_CHANGED} from './types';
import{Actions} from 'react-native-router-flux'
import firebase from 'firebase';

export const emailChanged =(text) => {
    return{
        type: EMAIL_CHANGED,
        payload: text
    }
};


export const passwordChanged =(text) => {
    return{
        type: PASSWORD_CHANGED,
        payload: text
    }
};

export const loginUser =({email,password}) => {
    return(dispatch)=> {
        dispatch({type:LOGIN_USER});
        firebase.auth().signInWithEmailAndPassword(email, password)
            .then(user => {
                loginUserSuccess(dispatch,user)
            })
            .catch(()=>{
                loginUserFail(dispatch)
            })
    }
};

export const signUpUser =({email,password}) => {
    return(dispatch)=> {
        dispatch({type:LOGIN_USER});
                firebase.auth().createUserWithEmailAndPassword(email,password)
                    .then(user => {
                        loginUserSuccess(dispatch,user)
                    })
                    .catch(()=>loginUserFail(dispatch))

    }
};


const loginUserSuccess = (dispatch,user) =>{
    dispatch({
        type: LOGIN_USER_SUCCESS, payload: user
    });
    Actions.main();
};

const loginUserFail =(dispatch) =>{
    dispatch({
        type: LOGIN_USER_FAIL
    });

};

export const emailSignUpChanged =(text) => {
    return{
        type: EMAIL_SIGNUP_CHANGED,
        payload: text
    }
};

export const passwordSignUpChanged =(text) => {
    return{
        type: PASSWORD_SIGNUP_CHANGED,
        payload: text
    }
};

export const passwordConfChanged =(text) => {
    return{
        type: PASSWORDCONF_CHANGED,
        payload: text
    }
};