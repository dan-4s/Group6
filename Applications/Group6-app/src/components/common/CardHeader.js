import React, {Component} from "react";
import {StyleSheet, Text, View} from "react-native";
import Card from "./Card";


const CardHeader = (props) =>{
  return(
      <View style ={[styles.container,props.style]}>
          {props.children}
      </View>
  )
};

const styles = StyleSheet.create({
        container: {
            borderBottomWidth:1,
            padding: 5,
            backgroundColor: '#fff',
            justifyContent:'flex-start',
            flexDirection: 'row',
            borderColor: '#ddd',
            position: 'relative',
        },
    }
);

export default CardHeader