import { faPlus, faX } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { toast } from "react-toastify";
import { Badge, Button, Card, CardBody, CardHeader, Container, Input } from "reactstrap";
import { apiDelete, getJSON, post, toastError } from "../../API/API";
import { ApplicationInfo, RouteInfo, ServiceConfigurationInfo, UserRegistrationInfo } from "../../API/Types";

const ApplicationConfiguration: React.FC = () => {

    const { uuid } = useParams();

    const [serviceConfigurations, setServiceConfigurations] = useState<ServiceConfigurationInfo[]>([]);

    useEffect(() => {
        getJSON<ServiceConfigurationInfo[]>("/api/service-configuration/")
            .then(e => setServiceConfigurations(e.filter(sc => sc.verified)))
            .catch(toastError("Unable to fetch Service Configurations"));
    }, []);

    const getServiceConfiguration = (ureg: UserRegistrationInfo): ServiceConfigurationInfo | null => {
        const scs = serviceConfigurations.filter(sc => sc.uuid === ureg.serviceConfiguration);
        if (scs) {
            return scs[0];
        } else {
            return null;
        }
    }

    const [application, setApplication] = useState<ApplicationInfo>();

    useEffect(() => {
        getJSON<ApplicationInfo>(`/api/application/${uuid}`)
            .then(setApplication)
            .catch(toastError("Unable to fetch Application"));
    }, [uuid]);

    type RouteWithUserRegistrations = {
        route: RouteInfo,
        userRegistrations: UserRegistrationInfo[]
    }

    const [routes, setRoutes] = useState<RouteWithUserRegistrations[]>([]);

    // const updateRoutesAndRegistrations = () => {
    //     application &&
    //         getJSON<RouteInfo[]>(`/api/application/${application?.uuid}/route`)
    //             .then(data =>
    //                 Promise.all(
    //                     data.map(route => new Promise<RouteWithUserRegistrations>((resolve, reject) => {
    //                         getJSON<UserRegistrationInfo[]>(`/api/route/${route.uuid}/user-registration`)
    //                             .then(regs => resolve({
    //                                 route: route,
    //                                 userRegistrations: regs
    //                             }))
    //                             .catch(reject)
    //                     })))
    //                     .then(setRoutes))
    // };

    const updateRoutesAndRegistrations = async () => {
        if (!application) {
            return;
        }
        const data = await getJSON<RouteInfo[]>(`/api/application/${application?.uuid}/route`);
        const regs = [];
        for (const route of data) {
            regs.push({
                route: route,
                userRegistrations: await getJSON<UserRegistrationInfo[]>(`/api/route/${route.uuid}/user-registration`)
            })
        }
        setRoutes(regs);
    };

    useEffect(() => {
        updateRoutesAndRegistrations()
            .catch(console.error);
        //bruh
        //eslint-disable-next-line react-hooks/exhaustive-deps
    }, [application]);

    const deleteRegistration = (ur: UserRegistrationInfo) => {
        apiDelete(`/api/user-registration/${ur.uuid}`)
            .then(() => toast.success("Deleted Registration!", {
                theme: "colored"
            }))
            .catch(toastError("Unable to delete Registration"))
            .finally(updateRoutesAndRegistrations)
    };

    const [newSelect, setNewSelect] = useState<{ [key: string]: string }>({});

    const addRegistration = (route: RouteInfo) => {
        if (!newSelect[route.uuid]) {
            return;
        }
        post("/api/user-registration", {
            route: route.uuid,
            serviceConfiguration: newSelect[route.uuid]
        })
            .then(() => {
                let n = { ...newSelect };
                delete n[route.uuid];
                setNewSelect(n)
                toast.success("Configured Application!", {
                    theme: "colored"
                })
            })
            .catch(toastError("Unable to register"))
            .finally(updateRoutesAndRegistrations)
    }

    return (
        application &&
        <Container>
            <Container className="px-1">
                <h3>
                    {application.name}
                    {!application.published && <Badge className="mx-2 text-white" color="warning">Not Published</Badge>}
                </h3>
                <p className="text-muted">{application.description}</p>
            </Container>
            {
                routes.length === 0 &&
                <p>No routes exist for this application :(</p>
            }
            {
                routes
                    .sort((a, b) => a.route.uuid.localeCompare(b.route.uuid))
                    .map((route, index) =>
                        <Card key={index} className="my-3">
                            <CardHeader>
                                <legend className="mb-0">{route.route.name}</legend>
                                <p className="text-muted mb-0">{route.route.description}</p>
                            </CardHeader>
                            <CardBody>
                                {
                                    route.userRegistrations.map(ur => ({
                                        ...ur,
                                        service: getServiceConfiguration(ur)
                                    })).map((ur, jndex) =>
                                        <Card key={`${index}-${jndex}`} className="my-3">
                                            <CardHeader className="d-flex align-items-center">
                                                <span className="lead flex-grow-1">{ur.service?.description}</span>
                                                <Button size="sm" className="float-right btn-danger shadow-none" onClick={() => deleteRegistration(ur)}>
                                                    <FontAwesomeIcon icon={faX} />
                                                </Button>
                                            </CardHeader>
                                        </Card>
                                    )
                                }
                                {
                                    serviceConfigurations
                                        .filter(sc => !route.userRegistrations.map(getServiceConfiguration).includes(sc)).length > 0 &&
                                    <Card className="shadow-none mt-3">
                                        <CardHeader className="d-flex pl-0 shadow-none align-items-center bg-secondary">
                                            <Input type="select" className="border rounded mr-3 px-3" onChange={e => setNewSelect(old => ({
                                                ...old,
                                                [route.route.uuid]: e.target.value
                                            }))}>
                                                <option value="" hidden>Add new Registration</option>
                                                {
                                                    serviceConfigurations
                                                        .filter(sc => !route.userRegistrations.map(getServiceConfiguration).includes(sc))
                                                        .map((service, index) =>
                                                            <option key={index} value={service.uuid}>{service.description}</option>
                                                        )
                                                }
                                            </Input>
                                            <Button size="sm" className="btn-success shadow-none" disabled={!newSelect[route.route.uuid]} onClick={() => addRegistration(route.route)}>
                                                <FontAwesomeIcon icon={faPlus} />
                                            </Button>
                                        </CardHeader>
                                    </Card>
                                }
                            </CardBody>
                        </Card>
                    )
            }
        </Container>
    ) || <></>
}

export default ApplicationConfiguration;