import { faPlus } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import { Badge, Button, Card, CardBody, CardHeader, Col, Container, Row, Table } from "reactstrap";
import { apiDelete, getJSON } from "../../API/API";
import { ServiceConfigurationInfo } from "../../API/Types";
import ConfirmButton from "../../components/ConfirmButton";

const ServiceConfigurations: React.FC = () => {

    const [serviceConfigurations, setServiceConfigurations] = useState<ServiceConfigurationInfo[]>([]);

    const updateServiceConfigurations = () => {
        getJSON<ServiceConfigurationInfo[]>("/api/service-configuration/")
            .then(setServiceConfigurations)
            .catch(e => toast.error("Unable to fetch Service Configurations " + e, {
                theme: "colored"
            }));
    }

    useEffect(updateServiceConfigurations, []);

    const deleteService = (con: ServiceConfigurationInfo) => {
        apiDelete(`/api/service-configuration/${con.uuid}`)
            .then(() => toast.success("Deleted Service Configuration!", {
                theme: "colored"
            }))
            .catch(e => toast.error("Unable to delete Service Configuration " + e, {
                theme: "colored"
            }))
            .finally(updateServiceConfigurations)
    }

    return (
        <Container>
            <Container className="d-flex my-2 p-1">
                <h3 className="flex-grow-1">Service Configurations</h3>
                <Link to="/service-configuration/create"><Button size="sm" color="primary" className="py-2">&nbsp;<FontAwesomeIcon icon={faPlus} />&nbsp;</Button></Link>
            </Container>
            <Row>
                {
                    serviceConfigurations
                        .filter(n => n.service.name !== "web")
                        .map((config, index) =>
                            config.service.id !== "web" &&
                            <Col key={index} className="py-3" xs={12} sm={12} md={6} lg={4} xl={4} xxl={3}>
                                <Card className="h-100">
                                    <CardHeader className="d-flex">
                                        <span>{config.description}</span>
                                        <Badge className="mx-2 py-1" color={config.verified ? "success" : "warning"}>{config.verified ? "Verified" : "Not Verified"}</Badge>
                                        <span className="flex-grow-1 text-right text-muted">{config.service.name}</span>
                                    </CardHeader>
                                    <CardBody className="d-flex flex-column">
                                        {
                                            config.properties.length > 0 &&
                                            <Table bordered size="sm">
                                                <thead>
                                                    <tr>
                                                        <th>Property</th>
                                                        <th>Value</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {
                                                        config.properties.map((prop, index) =>
                                                            <tr key={index}>
                                                                <th>{prop.name}</th>
                                                                <td>{prop.value}</td>
                                                            </tr>
                                                        )
                                                    }
                                                </tbody>
                                            </Table>
                                        }
                                        <Container className="d-flex p-0 mt-auto">
                                            <ConfirmButton id={`delete-${config.uuid}`} onClick={() => deleteService(config)} buttonClassName="flex-grow-1 btn-danger">Delete</ConfirmButton>
                                        </Container>
                                    </CardBody>
                                </Card>
                            </Col>
                        )
                }
            </Row>
        </Container>
    );
}

export default ServiceConfigurations;