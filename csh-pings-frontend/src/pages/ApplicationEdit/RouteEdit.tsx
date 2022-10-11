import { faX } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useEffect } from "react";
import { useState } from "react";
import { toast } from "react-toastify";
import { Button, Card, CardBody, CardHeader, Container, Input } from "reactstrap";
import { apiDelete, getJSON, post } from "../../API/API";
import { ApplicationInfo, RouteInfo } from "../../API/Types";
import ConfirmButton from "../../components/ConfirmButton";
import CopyButton from "../../components/CopyButton";

export interface RouteEditProps {
    application: ApplicationInfo
}

const RouteEdit: React.FC<RouteEditProps> = props => {

    const { application } = props;

    const [routes, setRoutes] = useState<RouteInfo[]>([]);

    const loadRoutes = () => {
        getJSON<RouteInfo[]>(`/api/application/${application.uuid}/route`)
            .then(setRoutes)
            .catch(e => toast.error("Unable to fetch Routes " + e, {
                theme: "colored"
            }));
    }

    useEffect(loadRoutes, [application]);

    const deleteRoute = (route: RouteInfo) => {
        apiDelete(`/api/application/${application.uuid}/route/${route.uuid}`)
            .catch(e => toast.error("Unable to delete Route " + e, {
                theme: "colored"
            }))
            .finally(loadRoutes);
    }

    const [routeName, setRouteName] = useState("");

    const [routeDesc, setRouteDesc] = useState("");

    const createRoute = () => {
        post(`/api/application/${application.uuid}/route`, {
            name: routeName,
            description: routeDesc
        })
            .then(() => {
                setRouteName("");
                setRouteDesc("");
            })
            .then(() => toast.success("Created Route!", {
                theme: "colored"
            }))
            .catch(e => toast.error("Unable to create Route " + e, {
                theme: "colored"
            }))
            .finally(loadRoutes);
    }

    return (
        <Card className="my-3">
            <CardHeader>Routes</CardHeader>
            <CardBody>
                {
                    routes
                        .sort((a, b) => a.name.localeCompare(b.name))
                        .map((route, index) =>
                            <Card key={index} className="mb-3">
                                <CardBody className="d-flex p-2">
                                    <div className="flex-grow-1">
                                        <h5 className="mb-0">{route.name}</h5>
                                        <p className="text-muted mb-0">{route.description}</p>
                                    </div>
                                    <CopyButton copyText={route.uuid} className="shadow-none btn-info ml-2" size="sm">Copy UUID</CopyButton>
                                    {
                                        !application.published &&
                                        <ConfirmButton placement="right" buttonClassName="shadow-none px-3 btn-sm ml-2" onClick={() => deleteRoute(route)} id={`r-delete-${index}`} confirmText="Delete"><FontAwesomeIcon icon={faX} /></ConfirmButton>
                                    }
                                </CardBody>
                            </Card>
                        )
                }
                {
                    application.published ?
                        <p>Cannot create routes while Application is published</p>
                        :
                        <Container className="pt-4 px-1">
                            <Input
                                type="text"
                                bsSize="sm"
                                placeholder="Name"
                                value={routeName}
                                onChange={e => setRouteName(e.target.value)}
                            />
                            <Input
                                type="text"
                                className="my-2"
                                bsSize="sm"
                                placeholder="Description"
                                value={routeDesc}
                                onChange={e => setRouteDesc(e.target.value)}
                            />
                            <Container className="d-flex">
                                <div className="flex-grow-1">&nbsp;</div>
                                <Button color="danger" size="sm" className="shadow-none" onClick={createRoute} disabled={!routeName || !routeDesc}>Create Route</Button>
                            </Container>
                        </Container>
                }
            </CardBody>
        </Card>
    )
}

export default RouteEdit;