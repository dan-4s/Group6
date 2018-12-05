import React,{Component} from 'react';
import{Text,TouchableWithoutFeedback,View} from 'react-native';
import CardHeader from './CardHeader'
import {Actions} from 'react-native-router-flux'

class ListItem extends Component{
    onRowPress(){
        Actions.SensorData({sensorData:this.props.data});
    }

    render(){
        return(
            <TouchableWithoutFeedback onPress={this.onRowPress.bind(this)}>
                <View>
                    <CardHeader>
                        <Text style={styles.titleStyle}>
                            {this.props.data.name}
                        </Text>
                    </CardHeader>
                </View>
            </TouchableWithoutFeedback>
        );
    }
}

const styles ={
    titleStyle:{
        fontSize:18,
        paddingLeft:15
    }
};

export default ListItem