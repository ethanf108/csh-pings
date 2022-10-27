import React, { useContext } from "react";
import {
  Badge,
  Collapse,
  Container,
  Nav,
  Navbar,
  NavbarBrand,
  NavbarToggler,
  NavItem,
} from "reactstrap";
import { NavLink } from "react-router-dom";
import Profile from "./Profile";
import WebNotifications from "./WebNotifications";
import { UserSettingsContext } from "../../pages/App/App";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faWarning } from "@fortawesome/free-solid-svg-icons";

const NavBar: React.FunctionComponent = () => {
  const [isOpen, setIsOpen] = React.useState<boolean>(false);

  const toggle = () => {
    setIsOpen(!isOpen);
  };

  const [userSettings] = useContext(UserSettingsContext);

  return (
    <Navbar color="primary" dark expand="lg" fixed="top">
      <NavbarBrand href="/" className="navbar-brand">Pings</NavbarBrand>
      <NavbarToggler onClick={toggle} />
      <Collapse isOpen={isOpen} navbar>
        <Nav navbar>
          <NavItem>
            <NavLink to="/application" className="nav-link">Applications</NavLink>
          </NavItem>
          <NavItem>
            <NavLink to="/service-configuration" className="nav-link">Service Configurations</NavLink>
          </NavItem>
        </Nav>
        <Nav navbar className="ml-auto">
          {
            userSettings.superuserMode &&
            <Container className="nav text-white">
              <Badge color="danger" className="d-flex">
                <FontAwesomeIcon size="2x" icon={faWarning} />
                <Container className="lead"><strong>Superuser Mode</strong></Container>
              </Badge>
            </Container>
          }
          <WebNotifications />
          <Profile />
        </Nav>
      </Collapse>
    </Navbar>
  );
};

export default NavBar;