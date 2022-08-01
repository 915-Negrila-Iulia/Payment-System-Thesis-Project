export class TransactionHistory {
    id?: number;
    type: string | undefined;
    action: string | undefined;
    amount: number | undefined;
    accountID: number | undefined;
    targetAccountID: number | undefined;
    status: string | undefined;
    nextStatus: string | undefined;
    transactionID: number | undefined;
    historyTimestamp: Date | undefined;
}
