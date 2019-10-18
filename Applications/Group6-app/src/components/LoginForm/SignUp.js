import React, {Component} from 'react'
import {View,Text} from 'react-native'
import Card from "../common/Card";
import CardHeader from "../common/CardHeader";
import Input from "../common/Input";
import Button from "../common/Button";
import Spinner from '../common/Spinner'
import {connect} from "react-redux"
import {emailSignUpChanged,passwordSignUpChanged,passwordConfChanged,signUpUser} from "../actions";

class SignUp extends Component{
    componentWillMount(){
        this.props.emailSignUpChanged("");
        this.props.passwordSignUpChanged("");
        this.props.passwordConfChanged("");
    }
    onRoomNameChange(text){
        this.props.emailSignUpChanged(text)
    }
    onPasswordChange(text){
        this.props.passwordSignUpChanged(text);
    }
    onPasswordConfChange(text){
        this.props.passwordConfChanged(text);
    }
    onButtonPress(){
        if(this.props.password !== this.props.passwordConf || this.props.passwordConf !=='' || this.props.passwordConf !==undefined){

        }
        else {
            const {email, password} = this.props;
            console.log({email, password});
            this.props.signUpUser({email, password})
        }
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
    renderPassError(){
        if(this.props.password !== this.props.passwordConf && this.props.passwordConf !=='' && this.props.passwordConf !==undefined){
            return(
                <View style={{backgroundColor:'white'}}>
                    <Text style={styles.errorTextStyle}>
                        Passwords must Match
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
                Create
            </Button>
        )
    }

    render(){
        return(
            <Card>
                <CardHeader>
                    <Input
                        label={"Email"}
                        placeholder={"email"}
                        onChangeText={this.onRoomNameChange.bind(this)}
                        value={this.props.SignUp}
                    />
                </CardHeader>


                <CardHeader>
                    <Input
                        secureTextEntry
                        label={"Password"}
                        placeholder={"Password"}
                        value={this.props.passwordSignUp}
                        onChangeText={this.onPasswordChange.bind(this)}
                    />
                </CardHeader>
                <CardHeader>
                    <Input
                        secureTextEntry
                        label={"Password Confirmation"}
                        placeholder={"Confirm password"}
                        value={this.props.passwordConf}
                        onChangeText={this.onPasswordConfChange.bind(this)}
                    />
                </CardHeader>
                {this.renderPassError()}

                {this.renderError()}

                <CardHeader>
                    {this.renderButton()}
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
        emailSignUp:state.auth.emailSignUp,
        passwordSignUp:state.auth.passwordSignUp,
        passwordConf:state.auth.passwordConf,
        error:state.auth.error,
        loading:state.auth.loading
    }
};

export default connect(mapStateToProps,{emailSignUpChanged,passwordSignUpChanged,passwordConfChanged,signUpUser})(SignUp)