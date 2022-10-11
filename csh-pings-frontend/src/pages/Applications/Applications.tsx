import { faArrowUpRightFromSquare, faGear, faPlus } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React, { useContext, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import { Badge, Button, Card, CardBody, CardHeader, Col, Container, Row } from "reactstrap";
import { getJSON } from '../../API/API';
import { ApplicationInfo, Paged } from '../../API/Types';
import Pager from '../../components/Pager';
import { UserSettingsContext } from '../App/App';

const Applications: React.FC = () => {

    const [userSettings] = useContext(UserSettingsContext);

    const [applications, setApplications] = useState<Paged<ApplicationInfo>>({
        elements: [],
        totalElements: 0
    });

    const [pagination, setPagination] = useState({
        page: 0,
        limit: 25
    });

    const changePage = (func: (i: number) => number) => {
        setPagination({
            page: func(pagination.page),
            limit: pagination.limit
        })
    }

    useEffect(() => {
        getJSON<Paged<ApplicationInfo>>("/api/application", {
            page: pagination.page,
            length: pagination.limit,
            hidden: userSettings.superuserMode
        })
        .then(setApplications)
        .catch(e=>toast.error("Unable to fetch Applications " + e, {
            theme: "colored"

        }))
    }, [pagination, userSettings.superuserMode]);

    return (
        <Container>
            <Container className="d-flex p-1">
                <h3 className="flex-grow-1">Applications</h3>
                {
                    userSettings.superuserMode &&
                    <Link to="/application/create">
                        <Button size="sm" color="primary" className="py-2">
                            &nbsp;
                            <FontAwesomeIcon icon={faPlus} />
                            &nbsp;
                        </Button>
                    </Link>
                }
            </Container>
            <Row>
                {
                    applications.elements
                    .filter(a=>a.published||userSettings.superuserMode)
                    .map((app, index) =>
                        <Col key={index} className="my-3">
                            <Card className="bg-secondary">
                                <CardHeader>
                                    {app.name}
                                    {
                                        app.webURL &&
                                        <a target="_blank" rel="noreferrer" href={app.webURL}>
                                            <FontAwesomeIcon className="ml-1" icon={faArrowUpRightFromSquare} />
                                        </a>
                                    }
                                    {!app.published && <Badge className="mx-2 text-white" color="warning">Not Published</Badge>}
                                </CardHeader>
                                <CardBody>
                                    <Link to={`/application/${app.uuid}/configure`}>
                                        <Container className="d-flex p-0">
                                            <Button size="sm" color="primary" className="flex-grow-1">
                                                Configure
                                                <FontAwesomeIcon icon={faGear} className="pl-1" />
                                            </Button>
                                        </Container>
                                    </Link>
                                    {
                                        userSettings.superuserMode &&
                                        <Link to={`/application/${app.uuid}/edit`}>
                                            <Container className="d-flex p-0 py-1">
                                                <Button size="sm" color="primary" className="flex-grow-1">
                                                    Edit
                                                </Button>
                                            </Container>
                                        </Link>
                                    }
                                </CardBody>
                            </Card>
                        </Col>
                    )
                }
            </Row>
            {
                Math.ceil(applications.totalElements / pagination.limit) > 1 &&
                <Container className="py-2 d-flex justify-content-center">
                    <Pager page={pagination.page} numPages={Math.ceil(applications.totalElements / pagination.limit)} changePage={changePage} />
                </Container>
            }
        </Container>
    );
};

export default Applications;