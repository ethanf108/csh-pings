import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useContext, useState } from "react";
import {
  DropdownItem,
  DropdownMenu,
  DropdownToggle,
  Label,
  UncontrolledDropdown,
} from "reactstrap";
import { faArrowUpRightFromSquare } from "@fortawesome/free-solid-svg-icons";
import { UserSettingsContext } from "../../pages/App/App";
import { getJSON, toastError } from "../../API/API";
import { UserInfo } from "../../API/Types";

const Profile: React.FunctionComponent = () => {

  const [userInfo, setUserInfo] = useState({
    username: "",
    fullName: "",
    rtp: false
  });

  React.useEffect(() => {
    getJSON<UserInfo>("/api/csh/user")
      .then(data => setUserInfo(data))
      .catch(toastError("Unable to fetch User Data"));
  }, []);

  const [userSettings, setUserSettings] = useContext(UserSettingsContext);

  return (
    <UncontrolledDropdown nav inNavbar>
      <DropdownToggle nav caret className="navbar-user">
        <img
          className="rounded-circle"
          src={`https://profiles.csh.rit.edu/image/${userInfo.username}`}
          alt=""
          aria-hidden={true}
          width={32}
          height={32}
        />
        {userInfo.fullName} ({userInfo.username})
        <span className="caret" />
      </DropdownToggle>
      <DropdownMenu>
        <DropdownItem href="/">Home</DropdownItem>
        <DropdownItem divider />
        <DropdownItem href="https://profiles.csh.rit.edu/" target="_blank">Profile&nbsp;<FontAwesomeIcon icon={faArrowUpRightFromSquare} /></DropdownItem>
        {
          userInfo.rtp &&
          <>
            <DropdownItem divider />
            <DropdownItem toggle={false} onClick={() => setUserSettings({ ...userSettings, superuserMode: !userSettings.superuserMode })}>
              <Label>{userSettings.superuserMode ? "Simple mode" : "Superuser mode"}</Label>
            </DropdownItem>
          </>
        }
      </DropdownMenu>
    </UncontrolledDropdown>
  );
};

export default Profile;