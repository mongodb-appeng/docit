import { RemoteMongoClient, Stitch, UserPasswordCredential } from 'mongodb-stitch-browser-sdk'
import config from '@/config'
import { ObjectId } from 'bson'

const client = Stitch.hasAppClient(config.stitchAppId)
    ? Stitch.getAppClient(config.stitchAppId)
    : Stitch.initializeAppClient(config.stitchAppId)

const mongodb = client.getServiceClient(RemoteMongoClient.factory, config.serviceName)
const db = mongodb.db(config.dbname)

const login = (username, password) => {
    return client.auth.loginWithCredential(new UserPasswordCredential(username, password))
}

const logout = () => { return client.auth.logout() }
const getUsersCollection = () => { return db.collection('users') }
const getDocumentsCollection = () => { return db.collection('documents') }
const getProjectsCollection = () => { return db.collection('projects') }
const getTasksCollection = () => { return db.collection('tasks') }
const getUserCollection = () => { return db.collection('users') }
const getObjectId = (oid) => {return new ObjectId(oid) }
const getCaseCollection = () => { return db.collection('cases') }
const getDataCountsCollection = () => { return db.collection('dataCounts') }

const findOneEmail = async (id) => {
    const docColl = getDocumentsCollection()
    return await docColl.findOne({_id: getObjectId(id._id)})
}

export const StitchServices = {
    client,
    mongodb,
    login,
    logout,
    getUsersCollection,
    getDocumentsCollection,
    getProjectsCollection,
    getTasksCollection,
    getUserCollection,
    getObjectId,
    getCaseCollection,
    getDataCountsCollection,
    findOneEmail
}