/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */
import React, {Component} from 'react';
import {StyleSheet, Text, View} from 'react-native';
import { Provider } from "react-redux";
import {createStore, applyMiddleware} from 'redux';
import firebase from 'firebase';
import ReduxThunk from 'redux-thunk';
import reducers from "./reducers";
import LoginForm from "./components/LoginForm/LoginForm"
import Router from "./Router"


class App extends Component
{
    componentWillMount(){
        firebase.initializeApp({
            apiKey: "AIzaSyCgSCcmYd6iIHyxF6YeQBAw-BNg5Pc7wc8",
            authDomain: "greenhousedata-cef98.firebaseapp.com",
            databaseURL: "https://greenhousedata-cef98.firebaseio.com",
            projectId: "greenhousedata-cef98",
            storageBucket: "greenhousedata-cef98.appspot.com",
            messagingSenderId: "526472420376"
        });
    }

  render() {
    return (
        <Provider store={createStore(reducers,{},applyMiddleware(ReduxThunk))}>
          <Router/>
        </Provider>
    );
  }
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

export default App