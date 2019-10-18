import _ from 'lodash';
import React, {Component} from 'react';
import {ListView, View} from 'react-native';
import {connect} from "react-redux"
import {greenHouseFetch} from "./actions/GreenHouseActions";
import ListItem from './common/ListItem';



class GreenHouseList extends Component{
    componentWillMount(){
        this.props.greenHouseFetch();
        this.createDataSource(this.props)
    }

    componentWillReceiveProps(nextProps){
        this.createDataSource(nextProps)
    }

    createDataSource({greenHouses}){
        const ds= new ListView.DataSource({
            rowHasChanged:(r1,r2)=> r1 !== r2
        });

        this.dataSource = ds.cloneWithRows(greenHouses)
    }


    renderRow(greenHouses){
        return <ListItem data={greenHouses}/>;
    }


    render(){
        return(
          <React.Fragment>
            <ListView
              enableEmptySections
              dataSource={this.dataSource}
              renderRow={this.renderRow}
            />
          </React.Fragment>

        );
    }
}

const mapStateToProps = (state) => {
    const greenHouses = _.map(state.greenHouses, (val, uid) => {
        return {...val, uid };
    });
    return {greenHouses};
};

export default connect(mapStateToProps,{greenHouseFetch})(GreenHouseList);