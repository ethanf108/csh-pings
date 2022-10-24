import { InputType } from "reactstrap/es/Input";

export type uuid = string;

export type Paged<T> = {
    totalElements: number,
    elements: T[]
}

export type UserInfo = {
    username: string,
    fullName: string,
    rtp: boolean
}

export type WebNotificationInfo = {
    uuid: uuid,
    body: string,
    routeUUID: uuid,
    applicationUUID: uuid,
    date: Date,
    unread: boolean
}

export type ApplicationInfo = {
    uuid: uuid,
    name: string,
    description: string,
    webURL: string,
    published: boolean,
    maintainers: string[]
}

export type ServiceInfo = {
    id: string,
    name: string,
    description: string
}

export type ConfigurablePropertyInfo = {
    id: string,
    name: string,
    description: string,
    value: string
}

export type ServiceConfigurationInfo = {
    uuid: uuid,
    description: string,
    service: ServiceInfo
    verified: boolean,
    properties: ConfigurablePropertyInfo[]
}

export type ServiceConfigurationProperty = {
    id: string,
    name: string,
    description: string,
    type: InputType,
    validationRegex: string,
    enumValues: string[]
}

export type RouteInfo = {
    uuid: uuid,
    name: string,
    description: string
}

export type UserRegistrationInfo = {
    uuid: uuid,
    route: uuid,
    serviceConfiguration: uuid
}

export interface ExternalTokenInfo {
    token: string,
    note: string
}

export interface MaintainerInfo {
    username: string
}

export interface ErrorInfo {
    message: string,
    stackTrace: string
}