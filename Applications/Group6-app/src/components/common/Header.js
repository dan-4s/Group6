import React from "react";
import {StyleSheet, Text, View} from "react-native";

const Header = (props) => {
    return (
        <View style={styles.container}>
            <Text style={styles.head}>{props.headerText}</Text>
        </View>
    )
};
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

    }
);

export default Header;