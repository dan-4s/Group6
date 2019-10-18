import React, {Component} from "react";
import {StyleSheet, Text, View, Image, ActivityIndicator} from "react-native";


const Spinner = (props) =>{
    return(
      <View style={styles.spinnerStyle}>
          <ActivityIndicator size={props.size || 'large'}/>
      </View>
    )
};

const styles = StyleSheet.create({
       spinnerStyle:{
           flex:1,
           justifyContent:"center",
           alignItems:"center"
       }

    }
);

export default Spinner