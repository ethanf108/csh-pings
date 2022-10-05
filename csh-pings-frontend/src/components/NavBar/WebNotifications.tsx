import React, { useEffect, useState } from "react";
import {
    Badge,
    DropdownItem,
    DropdownMenu,
    DropdownToggle,
    UncontrolledDropdown,
} from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBell, faCircle } from "@fortawesome/free-solid-svg-icons";
import { getJSON, post } from "../../API/API";
import { WebNotificationInfo } from "../../API/Types";

const WebNotifications: React.FC = () => {

    const [notifications, setNotifications] = useState<WebNotificationInfo[]>([]);

    useEffect(() => {
        getJSON<WebNotificationInfo[]>("/api/web-notification")
            .then(data =>
                data.map(n => ({
                    ...n,
                    date: new Date(n.date)
                }))
            )
            .then(setNotifications);
    }, []);

    const markAsRead = () => {
        post("/api/web-notification/read")
    }

    return (
        <UncontrolledDropdown nav inNavbar className="pt-2" onClick={markAsRead}>
            <DropdownToggle nav className="navbar">
                <FontAwesomeIcon icon={faBell} style={{
                    pointerEvents: "none"
                }} />
                {
                    notifications.filter(n => n.unread).length > 0 &&
                    <Badge className="ml-2 text-white" color="danger">{notifications.filter(n => n.unread).length}</Badge>
                }
            </DropdownToggle>
            <DropdownMenu>
                {
                    notifications
                    .sort((a,b)=>b.date.getTime()-a.date.getTime())
                        .map((item, index) =>
                            <DropdownItem key={index} disabled>
                                <FontAwesomeIcon icon={faCircle} className="mr-2 pl-0" color={item.unread ? "#B0197E" : "#FFFFFF"} />
                                {item.body}
                            </DropdownItem>
                        )
                }
            </DropdownMenu>
        </UncontrolledDropdown>
    )
}

export default WebNotifications;