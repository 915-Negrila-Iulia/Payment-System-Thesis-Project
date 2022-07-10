export class Audit {
    id?: number;
    objectID: number | undefined;
    objectType: string | undefined;
    operation: string | undefined;
    userID: number | undefined;
    timestamp: Date | undefined;
}
