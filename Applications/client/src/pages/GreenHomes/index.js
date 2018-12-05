import React, { Component } from 'react';
import {
  Button,
  Container,
  Card,
  Header,
  Segment,
} from 'semantic-ui-react';
import firebase from 'firebase';

class GreenHomes extends Component {
  constructor(props) {
    super(props);
    this.state = {
      token: '',
      GreenHomes: [],
    };
    this.makeCard = this.makeCard.bind(this);
    this.toggleFan = this.toggleFan.bind(this);
    this.makeNotification = this.makeNotification.bind(this);
  }

  componentDidMount() {
    firebase.database().ref('/users/TFSInAIyjZasPfyanDjsveMmdRH2/').on('value', (snapshot) => {
      this.setState({
        token: [snapshot.val().Token],
      });
    });

    firebase.database().ref('/users/TFSInAIyjZasPfyanDjsveMmdRH2/greenHouses/-LO6EC8taWQp_X6WGnS1').on('value', (snapshot) => {
      this.setState({
        GreenHomes: [snapshot.val()],
      });
      if (snapshot.val().Errors.error !== '') {
        this.makeNotification(snapshot.val().Errors.error);
      }

      if (snapshot.val().sensorData.Sensor1.humidity > 40 || snapshot.val().sensorData.Sensor1.humidity < 20){
        this.makeNotification("Humidity not within acceptable ranges");
      }

      if (snapshot.val().sensorData.Sensor1.temprature > 35 || snapshot.val().sensorData.Sensor1.temperature <20){
        this.makeNotification("Temperature not within acceptable ranges");
      }
    });
  }

  toggleFan() {
    firebase.database().ref('/users/TFSInAIyjZasPfyanDjsveMmdRH2/greenHouses/-LO6EC8taWQp_X6WGnS1/Button').update(
      {
        fanStatusNew: !this.state.GreenHomes[0].sensorData.Sensor1.fanStatus,
      },
    );
  }

  makeCard(house) {
    return (
      <Card>
        <Card.Content>
          <Card.Header
            style={{ textAlign: 'center' }}
          >
            {house.name}
          </Card.Header>
          <Card.Description style={{ textAlign: 'center' }}>
            Temperature: {house.sensorData.Sensor1.temperature}&#176;C
            <br/>
            Humidity: {house.sensorData.Sensor1.humidity}
            <br/>
            Fan Status: {String(house.sensorData.Sensor1.fanStatus)}
          </Card.Description>
        </Card.Content>
        <Card.Content extra style={{ textAlign: 'center' }}>
          <div className="ui center buttons">
            <Button
              basic
              color="green"
              onClick={this.toggleFan}
            >
              Toggle Fan
            </Button>
          </div>
        </Card.Content>
      </Card>
    );
  }

  makeNotification(error) {
    console.log("start request");
    fetch('https://fcm.googleapis.com/fcm/send', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'key=AAAAepQznBg:APA91bF0RrH9IZm-J5r1XnQaBYmeeokQ7h6rW1gV_GiWWUyzTC5ShddjE5BqOiG7kcMPD-rwZW61-1YqzZ_8GiBBdSM3q24yyeK0nUZiN2DWn5GgiPvpD2tgpPBzyeuw2qCe_oV5cIs_',
      },
      body: JSON.stringify({
        notification: {
          title: 'GreenHouse management System',
          body: error,
        },
        to: this.props.token,
      }),
    });
    console.log('made Request');
    firebase.database().ref('/users/TFSInAIyjZasPfyanDjsveMmdRH2/greenHouses/-LO6EC8taWQp_X6WGnS1/Errors').update(
      {
        error: '',
      },
    );
  }


  render() {
    return (
      <>
        <Segment vertical>
          <Container text style={{ textAlign: 'center', marginTop: '20px' }}>
            <Header as="h3" style={{ fontSize: '2em' }}>
              GreenHouses
            </Header>
            {this.state.GreenHomes.map(this.makeCard)}
          </Container>
        </Segment>
      </>
    );
  }
}

export default GreenHomes;
