import {
    GREENHOUSE_FETCH_SUCCESS,
    GREENHOUSE_DATA_FETCH_SUCCESS
} from "./types"
import firebase from 'firebase'


export const greenHouseFetch =() => {
    const { currentUser } = firebase.auth();
    return (dispatch) => {
        firebase.database().ref(`/users/${currentUser.uid}/greenHouses`).on('value',snapshot=>{
            dispatch({type:GREENHOUSE_FETCH_SUCCESS,payload:snapshot.val()})
        });
    };
};

export const sensorDataFetch =(uiddata) => {
    const { currentUser } = firebase.auth();
    return (dispatch) => {
        firebase.database().ref(`/users/${currentUser.uid}/greenHouses/${uiddata}/sensorData`).on('value',snapshot=>{
            dispatch({type:GREENHOUSE_DATA_FETCH_SUCCESS,payload:snapshot.val()})
        });
    };
};

