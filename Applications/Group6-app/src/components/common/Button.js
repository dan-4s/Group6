import React, {Component} from "react";
import {StyleSheet, Text, View, Image, TouchableOpacity} from "react-native";

const Button = (props) =>{
    return(
        <TouchableOpacity onPress={props.onPress} style={styles.buttonStyle}>
            <Text style={styles.buttonText}>{props.children}</Text>
        </TouchableOpacity>
    )
};

const styles = StyleSheet.create({
        buttonStyle: {
            flex:1,
            alignSelf:"stretch",
            backgroundColor: "#fff",
            borderRadius: 5,
            borderWidth: 1,
            borderColor: '#007aff',
            marginLeft:5,
            marginRight:5,
        },
        buttonText:{
            alignSelf:'center',
            color: "#007aff",
            fontSize:16,
            fontWeight: '600',
            paddingTop:10,
            paddingBottom:10
        }
    }
);


export default Button