import React, { createContext, Dispatch, SetStateAction, useEffect, useState } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Verify from "../Verify";
import Applications from "../Applications";
import ServiceConfigurations from "../ServiceConfigurations";
import ServiceConfigurationCreate from "../ServiceConfigurationCreate";
import ApplicationConfiguration from "../ApplicationConfiguration";
import ApplicationCreate from "../ApplicationCreate";
import ApplicationEdit from "../ApplicationEdit";
import Priviliged from "../../components/Privileged";
import NavBar from "../../components/NavBar";
import { Container } from "reactstrap";
import Splash from "../Splash";
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

interface UserSettings {
    superuserMode: boolean
}

export const UserSettingsContext =
    createContext<[UserSettings, Dispatch<SetStateAction<UserSettings>>]>([
        null as unknown as UserSettings,
        null as unknown as Dispatch<SetStateAction<UserSettings>>]);

/*
TODO
Rename tabs / names to be more understandable
Maybe help / splash page?

*/
const App: React.FC = () => {

    const [userSettings, setUserSettings] = useState(
        window.localStorage.getItem("user_settings") ?
            JSON.parse(window.localStorage.getItem("user_settings") || "") as UserSettings :
            {
                superuserMode: false
            } as UserSettings
    );

    useEffect(() => {
        window.localStorage.setItem("user_settings", JSON.stringify(userSettings));
    }, [userSettings])

    return (
        <UserSettingsContext.Provider value={[userSettings, setUserSettings]}>
            <Router>
                <Container className="main" fluid>
                    <NavBar />
                    <ToastContainer theme="colored" hideProgressBar newestOnTop className="py-4 my-4" />
                    <Container>
                        <Routes>
                            <Route path="/" element={<ServiceConfigurations />} />
                            <Route path="/application" element={<Applications />} />
                            <Route path="/application/:uuid/configure" element={<ApplicationConfiguration />} />
                            <Route path="/application/create" element={<Priviliged><ApplicationCreate /></Priviliged>} />
                            <Route path="/application/:uuid/edit" element={<Priviliged><ApplicationEdit /></Priviliged>} />
                            <Route path="/verify" element={<Verify />} />
                            <Route path="/service-configuration" element={<ServiceConfigurations />} />
                            <Route path="/service-configuration/create" element={<ServiceConfigurationCreate />} />
                            <Route path="*" element={<Splash />} />
                        </Routes>
                    </Container>
                </Container>
            </Router>
        </UserSettingsContext.Provider>
    );
}

export default App;