import React from "react";
import {
  Collapse,
  Nav,
  Navbar,
  NavbarBrand,
  NavbarToggler,
  NavItem,
} from "reactstrap";
import { NavLink } from "react-router-dom";
import Profile from "./Profile";
import WebNotifications from "./WebNotifications";

const NavBar: React.FunctionComponent = () => {
  const [isOpen, setIsOpen] = React.useState<boolean>(false);

  const toggle = () => {
    setIsOpen(!isOpen);
  };

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
          <WebNotifications />
          <Profile />
        </Nav>
      </Collapse>
    </Navbar>
  );
};

export default NavBar;