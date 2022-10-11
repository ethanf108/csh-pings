import { faCheck, faX } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { Button, Card, CardBody, CardHeader, Container } from "reactstrap";
import { getJSON, patch } from "../../API/API";
import { ApplicationInfo, MaintainerInfo, UserInfo } from "../../API/Types";
import UserSearch from "../../components/UserSearch";

export interface MaintainerEditProps {
    application: ApplicationInfo
}

const MaintainerEdit: React.FC<MaintainerEditProps> = props => {

    const { application } = props;

    const [maintainers, setMaintainers] = useState<MaintainerInfo[]>([]);

    const loadMaintainers = () => {
        getJSON<MaintainerInfo[]>(`/api/application/${application.uuid}/maintainer`)
            .then(setMaintainers)
            .catch(e => toast.error("Unable to fetch Maintainers " + e, {
                theme: "colored"
            }));
    }

    useEffect(loadMaintainers, [application]);

    const [users, setUsers] = useState<UserInfo[]>([]);

    const loadUsers = async () => {
        let ret = [];
        for (const maintainer of maintainers) {
            ret.push(await getJSON<UserInfo>(`/api/csh/user/${maintainer.username}`));
        }
        setUsers(ret);
    }

    useEffect(() => {
        loadUsers();
        //eslint-disable-next-line
    }, [maintainers]);

    const saveMaintainers = () => {
        patch(`/api/application/${application.uuid}/maintainer`, users.map(u => ({
            username: u.username
        })))
            .then(loadMaintainers)
            .catch(e => toast.error("Unable to edit Maintainers " + e, {
                theme: "colored"
            }));
    }

    return (
        <Card className="my-3">
            <CardHeader>Maintainers</CardHeader>
            <CardBody>
                <UserSearch users={[users, setUsers]} />
                <Container className="mt-4">
                    <Container className="d-flex p-0">
                        {
                            (users.length !== maintainers.length || !users.map((u, i) => [u.username, maintainers[i].username]).map(p => p[0] === p[1]).reduce((a, b) => a && b, true)) &&
                            <>
                                <div className="flex-grow-1">&nbsp;</div>
                                <Button size="sm" className="mx-2 shadow-none" onClick={loadUsers}><FontAwesomeIcon icon={faX} /></Button>
                                <Button size="sm" className="shadow-none" onClick={saveMaintainers}><FontAwesomeIcon icon={faCheck} /></Button>
                            </>
                        }
                    </Container>
                </Container>
            </CardBody>
        </Card>
    )
}

export default MaintainerEdit;