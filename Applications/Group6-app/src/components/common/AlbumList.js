import React, {Component} from "react";
import {StyleSheet, View, ScrollView} from "react-native";
import Albumdetails from "./AlbumDetails"

class AlbumList extends Component{
    state = {albums:[]};

    componentWillMount(){
        fetch('https://rallycoding.herokuapp.com/api/music_albums')
            .then((response) => response.json())
            .then((responseData)=>{
                this.setState({albums:responseData})
            });
    }

    renderAlbums(){
       return this.state.albums.map(album => <Albumdetails key={album.title} album={album}/>);
    }

    render() {
        return (
            <ScrollView>
                {this.renderAlbums()}
            </ScrollView>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        backgroundColor: '#f8f8f8',
        justifyContent: 'center',
        alignItems: 'center',
        height: 60,
        paddingTop: 15,
        shadowColor: '#000',
        shadowOffset: { width: 0,height: 20 },
        shadowOpacity: 0.9,
        elevation:20
    },

    head: {
        fontSize:30,
    },

});

export default AlbumList;