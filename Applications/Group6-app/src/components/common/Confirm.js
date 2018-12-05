import React from'react'
import {Text,View,Modal} from 'react-native'
import CardHeader from './CardHeader'
import Button from './Button'
const confirm =({children,visible,onAccept,onDecline}) =>{
    const{containerStyle,textStyle,cardSectionStyle}=styles;
    return(
        <Modal
            visible={visible}
            transparent
            animationType="slide"
            onRequestClose={()=>{}}
        >
            <View style={containerStyle}>
                <CardHeader style={cardSectionStyle}>
                    <Text style={textStyle}>
                        {children}
                    </Text>
                </CardHeader>
                <CardHeader>
                    <Button onPress={onAccept}>
                        Yes
                    </Button>

                    <Button onPress={onDecline}>
                        No
                    </Button>
                </CardHeader>
            </View>
        </Modal>
    )
};

const styles ={
  cardSectionStyle:{
      justifyContent:'center'
  },
  textStyle:{
      flex:1,
      fontSize:18,
      textAlign:'center',
      lineHeight: 40
  },
  containerStyle:{
      backgroundColor:'rgba(0,0,0,0.75)',
      position:'relative',
      flex:1,
      justifyContent:'center'
  }
};

export default confirm