export class User {
    id?: number;
    username: string | undefined;
    email: string | undefined;
    password: string | undefined;
    role: string | undefined;
    status= 'APPROVE';
    nextStatus= 'ACTIVE';
}
