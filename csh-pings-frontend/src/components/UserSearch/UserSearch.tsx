import { faX } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { Badge, Button, Container, Input } from "reactstrap";
import { getJSON, toastError } from "../../API/API";
import { UserInfo } from "../../API/Types";

const UserSearch: React.FC<{
    users: [UserInfo[], Dispatch<SetStateAction<UserInfo[]>>]
}> = ({ users: [users, setUsers] }) => {

    const [inputText, setInputText] = useState("");

    const [results, setResults] = useState<UserInfo[]>([]);

    useEffect(() => {
        if (!inputText || inputText.trim().length < 2) {
            setResults([]);
            return;
        }
        getJSON<UserInfo[]>("/api/csh/search", {
            query: inputText
        })
            .then(e => e.slice(0, 10))
            .then(setResults)
            .catch(toastError("Unable to search Users"));
    }, [inputText]);

    const addUser = (user: UserInfo) => {
        if (users.filter(u => u.username === user.username).length === 0) {
            setUsers(old => [...old, user]);
        }
        setInputText("");
    }

    const removeUser = (user: UserInfo) => {
        setUsers(old => old.filter(u => u.username !== user.username));
    }

    return (
        <Container className="px-0 dropdown">
            {
                users.map((user, index) =>
                    <p key={index} className="d-inline h5">
                        <Badge color="light" className="mr-2 text-dark font-weight-normal p-2 my-1">
                            {user.fullName} ({user.username})
                            <Button
                                size="sm"
                                className="ml-2 bg-transparent shadow-none p-0"
                                color=""
                                onClick={() => removeUser(user)}
                            >
                                <FontAwesomeIcon icon={faX} />
                            </Button>
                        </Badge>
                    </p>
                )
            }
            <Input
                type="text"
                className="mt-3"
                bsSize="sm"
                placeholder="Search"
                data-toggle="dropdown"
                value={inputText}
                onChange={e => setInputText(e.target.value)}
            />
            <ul className="dropdown-menu" role="menu" hidden={!inputText || inputText.length < 2 || results.length === 0}>
                {
                    results.map((user, index) =>
                        <Button key={index} size="sm" className="d-block bg-transparent shadow-none y-0" onClick={() => addUser(user)}>
                            {user.fullName} ({user.username})
                        </Button>
                    )
                }
            </ul>
        </Container>
    )
}

export default UserSearch;