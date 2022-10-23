import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { Button, Card, CardBody, CardHeader, Container, Form, FormText, Input } from "reactstrap";
import { getJSON, post, toastError } from "../../API/API";
import { ServiceConfigurationProperty, ServiceInfo } from "../../API/Types";

const ServiceConfigurationCreate: React.FC = () => {

    interface FormData {
        service: string | null,
        description: string,
        properties: (ServiceConfigurationProperty & { value: string })[] | null
    }

    const [formData, setFormData] = useState<FormData>({
        service: null,
        description: "",
        properties: null
    });

    const setService = (value: string) => {
        setFormData(old => ({
            ...old,
            service: value,
            description: "",
        }))
    }

    const setProperty = (id: string, value: string) => {
        if (!id) {
            return;
        }
        setFormData({
            ...formData,
            properties: formData.properties?.map(p => {
                if (p.id === id) {
                    p.value = value;
                }
                return p;
            }) || null
        })
    }

    const [services, setServices] = useState<ServiceInfo[]>([]);

    useEffect(() => {
        getJSON<ServiceInfo[]>("/api/service/")
            .then(data => setServices(data.filter(s => s.id !== "web")))
            .catch(toastError("Unable to fetch Services"));
    }, []);

    useEffect(() => {
        if (!formData.service) {
            return;
        }
        getJSON<ServiceConfigurationProperty[]>(`/api/service/${formData.service}/properties`)
            .then(data => {
                setFormData(old => ({
                    ...old,
                    properties: data.map(e => ({
                        ...e,
                        value: ""
                    }))
                }))
            })
            .catch(toastError("Unable to fetch Service Properties"));
    }, [formData.service]);

    const canSubmit = () => {
        if (!formData.service || !formData.properties) {
            return false;
        }
        return formData.properties.filter(prop => !prop.value).length === 0;
    }

    const submit = () => {
        if (!formData.properties) {
            return;
        }
        const body = {
            properties: formData.properties
                .map(prop => ({
                    [prop.id]: prop.value
                }))
                .reduce((a, b) => ({
                    ...a,
                    ...b
                })),
            serviceId: formData.service,
            description: formData.description
        }
        post("/api/service-configuration/", body)
            .then(() => toast.success("Created Service Configuration!", {
                theme: "colored"
            }))
            .then(() => window.location.assign("/service-configuration"))
            .catch(toastError("Error creating Service Configuration"));
    }

    return (
        <Container>
            <Form>
                <Card>
                    <CardHeader>Service</CardHeader>
                    <CardBody>
                        <Input id="service" type="select" className="form-control border rounded px-2" onChange={e => setService(e.target.value)}>
                            <option value="" hidden>Select option ...</option>
                            {
                                services.map((service, index) =>
                                    <option key={index} value={service.id}>{service.name}</option>
                                )
                            }
                        </Input>
                    </CardBody>
                </Card>
                <Card className="my-3">
                    <CardHeader>Description</CardHeader>
                    <CardBody className="pt-0">
                        <Input
                            id="sc-c-description"
                            type="text"
                            onChange={e => setFormData({
                                ...formData,
                                description: e.target.value
                            })}
                            placeholder="Description"
                            value={formData.description}
                        />
                        <FormText>Short name for this configuration</FormText>
                    </CardBody>
                </Card>
                {
                    formData.properties && formData.properties.map((prop, index) =>
                        <Card key={index} className="my-3">
                            <CardHeader>{prop.name}</CardHeader>
                            <CardBody className="pt-0">
                                {
                                    prop.type === "select" ?
                                        <Input
                                            id={`sc-c-${formData.service}-prop-${prop.name}`}
                                            type={prop.type}
                                            onChange={e => setProperty(prop.id, e.target.value)}
                                            placeholder={prop.name}
                                        >
                                            <option value="" hidden>Select option ...</option>
                                            {
                                                prop.enumValues.map((option, index) =>
                                                    <option key={index} value={option}>{option}</option>
                                                )
                                            }
                                        </Input>
                                        :
                                        <Input
                                            id={`sc-c-${formData.service}-prop-${prop.name}`}
                                            type={prop.type}
                                            onChange={e => setProperty(prop.id, e.target.value)}
                                            placeholder={prop.name}
                                            value={prop.value}
                                        />
                                }
                                <FormText dangerouslySetInnerHTML={{__html: prop.description}} />
                            </CardBody>
                        </Card>
                    )
                }
                <Container className="d-flex px-0">

                    <Button disabled={!canSubmit()} onClick={submit} size="sm" color="danger" className="flex-grow-1">Submit</Button>
                </Container>
            </Form>
        </Container>
    )
}

export default ServiceConfigurationCreate;