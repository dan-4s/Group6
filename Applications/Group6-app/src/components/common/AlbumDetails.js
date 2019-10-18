import React, {Component} from "react";
import {StyleSheet, Text, View, Image} from "react-native";
import Card from "./Card"
import CardHeader from "./CardHeader"

const AlbumDetails = ({album}) => {
    const {title,artist,thumbnail_image,image} = album;
    return (
        <Card>
            <CardHeader>
                <View style={styles.thumbnailContainerStyle}>
                    <Image style={styles.thumbnailStyle} source={{uri: thumbnail_image}}/>
                </View>
                <View style={styles.headerStyle}>
                    <Text>Song: {title}</Text>
                    <Text>Artist: {artist}</Text>
                </View>
            </CardHeader>
            <CardHeader>
                <Image style={styles.imageStyle} source={{uri: image}}/>
            </CardHeader>
            <CardHeader>
            </CardHeader>
        </Card>
    )

};

const styles = StyleSheet.create({
        headerStyle: {
            flexDirection:'column',
            justifyContent: 'space-around'
        },
        thumbnailStyle:{
            height:50,
            width:50,
        },
        thumbnailContainerStyle:{
            justifyContent: 'center',
            alignItems:'center',
            marginLeft:10,
            marginRight:10
        },
        imageStyle:{
            height:300,
            flex:1,
            width:null
        },
    }
);

export default AlbumDetails