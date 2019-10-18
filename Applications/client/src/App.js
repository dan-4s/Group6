import React, { Component } from 'react';
import {
  BrowserRouter,
  Route,
  Switch,
} from 'react-router-dom';
import './App.css';
import 'semantic-ui-css/semantic.min.css';
import firebase from 'firebase';
import ResponsiveContainer from './layouts/ResponsiveContainer';
import HomepageLayout from './pages/Home';
import GreenHomes from './pages/GreenHomes';


class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      Token: '',
    };
  }

  componentWillMount() {
    firebase.initializeApp({
      apiKey: 'AIzaSyCgSCcmYd6iIHyxF6YeQBAw-BNg5Pc7wc8',
      authDomain: 'greenhousedata-cef98.firebaseapp.com',
      databaseURL: 'https://greenhousedata-cef98.firebaseio.com',
      projectId: 'greenhousedata-cef98',
      storageBucket: 'greenhousedata-cef98.appspot.com',
      messagingSenderId: '526472420376',
    });
    const messaging = firebase.messaging();
    messaging.requestPermission()
      .then((response) => {
        console.log('request granted');
        return messaging.getToken().then((token) => {
          firebase.database().ref('/users').update(
            {
              Token: token,
            },
          );
          this.setState({
            Token: token,
          });
          return token;
        });
      })
      .catch((err) => {
        console.log('Unable to get permission to notify.', err);
      });
  }

  render() {
    return (
      <BrowserRouter>
        <ResponsiveContainer>
          <Switch>
            <Route exact path="/" component={HomepageLayout} />
            <Route path="/GreenHomes" render={() => <GreenHomes token={this.state.Token} />} />
          </Switch>
        </ResponsiveContainer>
      </BrowserRouter>
    );
  }
}

export default App;
