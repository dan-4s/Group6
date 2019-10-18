import React, {Component} from 'react'
import {View,Text} from 'react-native'
import Card from "../common/Card";
import CardHeader from "../common/CardHeader";
import Input from "../common/Input";
import Button from "../common/Button";
import Spinner from '../common/Spinner'
import {connect} from "react-redux"
import {emailChanged,passwordChanged,loginUser} from "../actions";
import {Actions} from "react-native-router-flux";

class LoginForm extends Component{


    onEmailChange(text){
        this.props.emailChanged(text)
    }

    onPasswordChange(text){
        this.props.passwordChanged(text);
    }
    onButtonPress(){
        const{email, password} = this.props;
        this.props.loginUser({email,password})
    }
    onSignUpPress(){
        Actions.SignUp()
    }

    renderError(){
        if(this.props.error){
            return(
                <View style={{backgroundColor:'white'}}>
                    <Text style={styles.errorTextStyle}>
                        {this.props.error}
                    </Text>
                </View>
            )
        }
    }

    renderButton(){
        if(this.props.loading){
            return <Spinner size={"large"}/>
        }
        return (
            <Button
                onPress={this.onButtonPress.bind(this)}
            >
            Login
            </Button>
        )
    }

    render(){
        return(
            <Card>
                <CardHeader>
                    <Input
                        label={"Email"}
                        placeholder={"email@gmail.com"}
                        onChangeText={this.onEmailChange.bind(this)}
                        value={this.props.email}
                    />
                </CardHeader>

                <CardHeader>
                    <Input
                        secureTextEntry
                        label={"Password"}
                        placeholder={"password"}
                        value={this.props.password}
                        onChangeText={this.onPasswordChange.bind(this)}
                    />
                </CardHeader>

                {this.renderError()}

                <CardHeader>
                    {this.renderButton()}
                </CardHeader>
                <CardHeader>
                    <Button
                        onPress={this.onSignUpPress.bind(this)}
                    >
                        Sign Up
                    </Button>
                </CardHeader>
            </Card>
        );
    }
}

const styles = {
    errorTextStyle:{
        fontSize:20,
        alignSelf:'center',
        color:'red'
    }
};

const mapStateToProps= state =>{
    return{
        email:state.auth.email,
        password:state.auth.password,
        error:state.auth.error,
        loading:state.auth.loading
    }
};

export default connect(mapStateToProps,{emailChanged,passwordChanged,loginUser})(LoginForm)