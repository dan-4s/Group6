import React,{Component} from 'react';
import {Text, View,UIManager, LayoutAnimation, TouchableWithoutFeedback } from "react-native";

import CardHeader from "./CardHeader";
import Button from "./Button";
import firebase from 'firebase';



class ListItem extends Component{

    componentWillMount(){
        this.state= {expanded:false};
        console.log(this.state)
    }

    componentDidUpdate(){
        UIManager.setLayoutAnimationEnabledExperimental && UIManager.setLayoutAnimationEnabledExperimental(true);
        LayoutAnimation.spring();
    }
    renderDescription(){
        if(this.state.expanded===true) {
            console.log("itChanged");
            return (
                <CardHeader>
                    <Text style={styles.leftStyle}>Temperature: {this.props.data.temperature} &#176;C</Text>
                    <Text style={styles.rightStyle}>Humidity: {this.props.data.humidity}</Text>
                    <Text style={styles.rightStyle}>Fan Status: {String(this.props.data.fanStatus)}</Text>
                </CardHeader>
            )
        }
    }
    toggleFan(){
      firebase.database().ref(`/users/TFSInAIyjZasPfyanDjsveMmdRH2/greenHouses/-LO6EC8taWQp_X6WGnS1/Button`).update(
        {
          fanStatusNew: !this.props.data.fanStatus
        }
      )
    };
    render(){
        console.log(this.props.data);
        return(
            <TouchableWithoutFeedback
              onPress={()=>{
                this.setState({expanded:!this.state.expanded});
                console.log(this.state.expanded)
              }}>
                <View style={styles.viewStyle}>
                    <CardHeader>
                         <Text style={styles.titleStyle}>
                             {this.props.data.uid}
                         </Text>
                    </CardHeader>
                    {this.renderDescription()}
                  <Button
                    onPress={this.toggleFan.bind(this)}
                  >
                    Toggle Fan
                  </Button>
                </View>
            </TouchableWithoutFeedback>

        );
    }
}


const styles ={
  titleStyle:{
      fontSize:18,
      paddingLeft:15
  },
    viewStyle:{
        marginBottom:15,
        flex: 1
    },
    leftStyle:{
        paddingLeft:15
    },
    rightStyle:{
        paddingLeft:15
    },
};



export default ListItem;