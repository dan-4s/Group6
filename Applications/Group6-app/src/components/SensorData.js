import _ from 'lodash';
import React, {Component} from 'react'
import {ListView} from 'react-native'
import {connect} from "react-redux"
import {sensorDataFetch} from "./actions/GreenHouseActions";
import ListItem from "./common/Sensordatalist";


class SensorData extends Component{

    componentWillMount(){
        this.props.sensorDataFetch(this.props.sensorData.uid);
        this.createDataSource(this.props)
    }

    componentWillReceiveProps(nextProps){
        this.createDataSource(nextProps)
    }

    createDataSource({data}){
        const ds= new ListView.DataSource({
            rowHasChanged:(r1,r2)=> r1 !== r2
        });

        this.dataSource = ds.cloneWithRows(data)
    }

    renderRow(data){
        return <ListItem data={data}/>;
    }


    render(){
        return(
            <ListView
                enableEmptySections
                dataSource={this.dataSource}
                renderRow={this.renderRow}
            />
        );
    }
}



const mapStateToProps = (state)=> {
    const data = _.map(state.sensorData, (val, uid) => {
        return {...val, uid };
    });
    return {data};
};

export default connect(mapStateToProps,{sensorDataFetch}) (SensorData);