export class Transaction {
    id?: number;
    type: string | undefined;
    action: string | undefined;
    amount: number | undefined;
    accountID: number | undefined;
    targetAccountID: number | undefined;
    status: string | undefined;
    nextStatus: string | undefined;
    targetIban: string | undefined;
}
