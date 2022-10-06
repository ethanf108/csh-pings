import { faPlus } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Badge, Button, Card, CardBody, CardHeader, Col, Container, Row, Table } from "reactstrap";
import { apiDelete, getJSON } from "../../API/API";
import { ServiceConfigurationInfo } from "../../API/Types";
import ConfirmButton from "../../components/ConfirmButton";

const ServiceConfigurations: React.FC = () => {

    const [serviceConfigurations, setServiceConfigurations] = useState<ServiceConfigurationInfo[]>([]);

    const updateServiceConfigurations = () => {
        getJSON<ServiceConfigurationInfo[]>("/api/service-configuration/").then(setServiceConfigurations);
    }

    useEffect(updateServiceConfigurations, []);

    const deleteService = (con: ServiceConfigurationInfo) => {
        apiDelete(`/api/service-configuration/${con.uuid}`)
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
                            <Col key={index} className="py-3">
                                <Card>
                                    <CardHeader>
                                        {config.description}
                                        <Badge className="mx-2" color={config.verified ? "success" : "warning"}>{config.verified ? "Verified" : "Not Verified"}</Badge>
                                    </CardHeader>
                                    <CardBody>
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
                                                                <th>{prop.description}</th>
                                                                <td>{prop.value}</td>
                                                            </tr>
                                                        )
                                                    }
                                                </tbody>
                                            </Table>
                                        }
                                        <Container className="d-flex p-0">
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