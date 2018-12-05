import React from 'react';
import {Scene,Router} from 'react-native-router-flux'
import LoginForm from './components/LoginForm/LoginForm'
import GreenHouseList from './components/GreenHouseList'
import SensorData from './components/SensorData'
import SignUp from './components/LoginForm/SignUp'
const RouterComponent= () =>{
    return(
        <Router>
            <Scene
            key="root"
            titleStyle={{alignSelf: 'center'}}
            hideNavBar
            >
                <Scene key="Auth">
                    <Scene
                        key="login"
                        component={LoginForm}
                        title="Please Login"
                        titleStyle={{ textAlign: 'center', flex: 1 }}
                        initial
                    >
                    </Scene>
                    <Scene
                        key="SignUp"
                        component={SignUp}
                        title="SignUp"
                        titleStyle={{ textAlign: 'center', flex: 1 }}
                    >
                    </Scene>
                </Scene>
                <Scene key="main">
                    <Scene
                        key="greenHouseList"
                        component={GreenHouseList}
                        title="Green Houses"
                        initial
                        >
                    </Scene>
                    <Scene
                        key="SensorData"
                        component={SensorData}
                        title="Data"
                    >
                    </Scene>
                </Scene>
            </Scene>
        </Router>
    );
};

export default RouterComponent